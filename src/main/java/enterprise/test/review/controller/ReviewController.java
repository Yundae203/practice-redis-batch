package enterprise.test.review.controller;

import enterprise.test.collabo.ProductReviewService;
import enterprise.test.common.CustomClock;
import enterprise.test.infra.s3.AmazonS3;
import enterprise.test.review.dto.ReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ReviewController {

    private final ProductReviewService productReviewService;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @PostMapping("/{productId}/reviews")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveReview(@PathVariable("productId") Long productId, @RequestBody ReviewRequest reviewRequest) {
        String imageUrl = uploadImageToS3(reviewRequest.image());
        productReviewService.saveProductReview(reviewRequest, productId, imageUrl);
    }

    private String uploadImageToS3(MultipartFile image) {
        if (image != null) {
            File file = new File(image.getOriginalFilename());
            String fileName = image.getOriginalFilename();
            amazonS3.putObject(bucketName, fileName, file);

            return amazonS3.getUrl(bucketName, fileName);
        }
        return null;
    }
}
