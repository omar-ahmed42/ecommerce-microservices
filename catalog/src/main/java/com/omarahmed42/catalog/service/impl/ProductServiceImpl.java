package com.omarahmed42.catalog.service.impl;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.Uuid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.omarahmed42.catalog.dto.message.Message;
import com.omarahmed42.catalog.dto.request.ProductCreation;
import com.omarahmed42.catalog.dto.request.QueryFilter;
import com.omarahmed42.catalog.dto.response.CategoryHierarchyResponse;
import com.omarahmed42.catalog.dto.response.PaginationResult;
import com.omarahmed42.catalog.dto.response.ProductMediaResponse;
import com.omarahmed42.catalog.dto.response.ProductResponse;
import com.omarahmed42.catalog.enums.AttachmentStatus;
import com.omarahmed42.catalog.enums.AttachmentType;
import com.omarahmed42.catalog.exception.ForbiddenProductAccessException;
import com.omarahmed42.catalog.exception.InvalidInputException;
import com.omarahmed42.catalog.exception.MaxProductMediaReachedException;
import com.omarahmed42.catalog.exception.ProductNotFoundException;
import com.omarahmed42.catalog.exception.UnsupportedMediaExtensionException;
import com.omarahmed42.catalog.exception.generic.InternalServerErrorException;
import com.omarahmed42.catalog.mapper.CategoryMapper;
import com.omarahmed42.catalog.mapper.ProductMapper;
import com.omarahmed42.catalog.mapper.ProductMediaMapper;
import com.omarahmed42.catalog.message.payload.ProductCreatedPayload;
import com.omarahmed42.catalog.message.payload.item.PricedItem;
import com.omarahmed42.catalog.message.producer.MessageSender;
import com.omarahmed42.catalog.model.Attachment;
import com.omarahmed42.catalog.model.Category;
import com.omarahmed42.catalog.model.Product;
import com.omarahmed42.catalog.model.ProductMedia;
import com.omarahmed42.catalog.repository.AttachmentRepository;
import com.omarahmed42.catalog.repository.CategoryRepository;
import com.omarahmed42.catalog.repository.ProductMediaRepository;
import com.omarahmed42.catalog.repository.ProductRepository;
import com.omarahmed42.catalog.service.FileService;
import com.omarahmed42.catalog.service.ProductService;
import com.omarahmed42.catalog.utils.PageUtils;
import com.omarahmed42.catalog.utils.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final MessageSender messageSender;

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private final FileService fileService;
    private final AttachmentRepository attachmentRepository;
    private final ProductMediaRepository productMediaRepository;
    private final ProductMediaMapper productMediaMapper;

    private static final String THUMBNAIL_PREFIX = "thumbnail-";
    private static final String MEDIA_PREFIX = "media-";

    private static final String[] IMAGE_EXTENSIONS = { "png", "jpg" };

    @Value("${storage.path}")
    private String storagePath;

    @Override
    @Transactional
    public ProductResponse addProduct(@Valid ProductCreation productCreation) {
        log.info("Creating a product with name {} and description {} and price {} and category id {}",
                productCreation.name(), productCreation.description(), productCreation.price(),
                productCreation.categoryId());
        Product product = productMapper.toEntity(productCreation);
        String subject = SecurityUtils.getSubject();
        log.info("Subject {}", subject);
        product.setSellerId(subject);
        product = productRepository.save(product);

        emitProductCreatedEvent(product);
        return productMapper.toProductResponse(product);
    }

    private void emitProductCreatedEvent(Product product) {
        ProductCreatedPayload payload = new ProductCreatedPayload();
        payload.setProductId(product.getId());
        payload.setStock(0);
        payload.setCorrelationId(UUID.randomUUID().toString());

        Message<ProductCreatedPayload> message = new Message<>("ProductCreatedEvent", payload);
        messageSender.send(message);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        if (id == null)
            throw new InvalidInputException("Product ID cannot be empty");
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toProductResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResult<ProductResponse> getProducts(QueryFilter filter) {
        PageRequest page = PageUtils.getPages(filter);

        if (StringUtils.isNotBlank((filter.getSearch()))) {
            // TODO: Test search performance using SQL and using Elasticsearch
            Page<Product> productsPage = productRepository.findByNameContaining(filter.getSearch(), page);
            return toProductResponsePage(productsPage);
        }

        Page<Product> productsPage = productRepository.findAll(page);
        return toProductResponsePage(productsPage);
    }

    private PaginationResult<ProductResponse> toProductResponsePage(Page<Product> productsPage) {
        return new PaginationResult<>(productMapper.toProductResponseList(productsPage.getContent()),
                productsPage.getNumberOfElements(),
                productsPage.getTotalElements(),
                productsPage.getTotalPages(),
                productsPage.getNumber());

    }

    @Override
    public List<PricedItem> getPricedProducts(List<Long> productsIds) {
        if (productsIds == null || productsIds.isEmpty())
            return Collections.emptyList();

        List<Long> filteredProductIds = productsIds.stream().filter(Objects::nonNull).toList();
        if (filteredProductIds.isEmpty())
            return Collections.emptyList();

        List<Product> pricedProducts = productRepository.findAllIdAndPriceByIdIn(filteredProductIds);
        return productMapper.toPricedItemList(pricedProducts);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryHierarchyResponse getProductCategoryHierarchy(Long id) {
        ProductResponse product = getProduct(id);
        Category category = categoryRepository.findCategoryHierarchy(product.categoryId());
        return categoryMapper.toCategoryHierarchyResponse(category);
    }

    @Override
    @Transactional
    public Long saveThumbnail(MultipartFile multipartFile, Long productId) {
        SecurityUtils.throwIfNotAuthenticated();

        String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (!isValidExtension(fileExtension, AttachmentType.IMAGE))
            throw new UnsupportedMediaExtensionException(fileExtension + " extension is not supported");

        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        throwIfNotOwner(product, SecurityUtils.getSubject());

        String filename = THUMBNAIL_PREFIX + Uuid.randomUuid().toString() + FilenameUtils.EXTENSION_SEPARATOR_STR
                + fileExtension;

        String fileUrl = storagePath + File.separator + filename;

        Attachment thumbnail = new Attachment();
        thumbnail.setExtension(fileExtension);
        thumbnail.setName(filename);
        thumbnail.setSize(multipartFile.getSize());
        thumbnail.setStatus(AttachmentStatus.UPLOADING);
        thumbnail.setUrl(fileUrl);
        thumbnail.setAttachmentType(AttachmentType.IMAGE);
        thumbnail = attachmentRepository.save(thumbnail);

        try {
            storeFile(multipartFile, thumbnail);
        } catch (Exception e) {
            log.error("Error while saving attachment: {}", e);
            throw new InternalServerErrorException("Could not store thumbnail");
        }

        thumbnail.setStatus(AttachmentStatus.COMPLETED);
        product.setThumbnail(thumbnail);
        product = productRepository.save(product);
        thumbnail = attachmentRepository.save(thumbnail);
        return thumbnail.getId();
    }

    private void throwIfNotOwner(Product product, String userId) {
        if (!isProductOwner(product, userId))
            throw new ForbiddenProductAccessException("Forbidden: Cannot access product with id " + product.getId());
    }

    private boolean isProductOwner(Product product, String userId) {
        String productOwnerId = product.getSellerId();
        return productOwnerId.equals(userId);
    }

    private boolean isValidExtension(String fileExtension, AttachmentType attachmentType) {
        if (AttachmentType.IMAGE.equals(attachmentType))
            return Arrays.stream(IMAGE_EXTENSIONS).anyMatch(ext -> ext.equalsIgnoreCase(fileExtension));
        return false;
    }

    public void storeFile(MultipartFile multipartFile, Attachment attachment) throws Exception {
        String fileUrl = attachment.getUrl();
        fileService.copy(multipartFile.getInputStream(), Path.of(fileUrl));
    }

    @Override
    public Long saveProductMedia(Long productId, MultipartFile multipartFile) {
        SecurityUtils.throwIfNotAuthenticated();

        String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (!isValidExtension(fileExtension, AttachmentType.IMAGE))
            throw new UnsupportedMediaExtensionException(fileExtension + " extension is not supported");

        Product product = productRepository
                .findById(productId)
                .orElseThrow(ProductNotFoundException::new);
        throwIfNotOwner(product, SecurityUtils.getSubject());

        long mediaCount = productMediaRepository.countByProduct(product);
        if (mediaCount >= 8L)
            throw new MaxProductMediaReachedException(
                    "Max number of media uploaded reached, to add more media, please, delete an existing media attachment");

        String filename = MEDIA_PREFIX + Uuid.randomUuid().toString() + FilenameUtils.EXTENSION_SEPARATOR_STR
                + fileExtension;

        String fileUrl = storagePath + File.separator + filename;

        Attachment attachment = new Attachment();
        attachment.setExtension(fileExtension);
        attachment.setName(filename);
        attachment.setSize(multipartFile.getSize());
        attachment.setStatus(AttachmentStatus.UPLOADING);
        attachment.setUrl(fileUrl);
        attachment.setAttachmentType(AttachmentType.IMAGE);
        attachment = attachmentRepository.save(attachment);

        try {
            storeFile(multipartFile, attachment);
        } catch (Exception e) {
            log.error("Error while saving attachment: {}", e);
            throw new InternalServerErrorException("Could not store thumbnail");
        }

        attachment.setStatus(AttachmentStatus.COMPLETED);
        attachment = attachmentRepository.save(attachment);

        ProductMedia productMedia = new ProductMedia();
        productMedia.setProduct(product);
        productMedia.setAttachment(attachment);
        productMedia = productMediaRepository.save(productMedia);

        return productMedia.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductMediaResponse> getProductMedia(Long productId) {
        List<ProductMedia> media = productMediaRepository
                .findAllByProduct(productRepository.getReferenceById(productId));

        return productMediaMapper.toProductMediaResponseList(media);
    }

}