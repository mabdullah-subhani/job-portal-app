 package com.demoproject.fileservice.config;

 import org.springframework.boot.context.properties.EnableConfigurationProperties;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
 import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
 import software.amazon.awssdk.regions.Region;
 import software.amazon.awssdk.services.s3.S3Client;
 import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@EnableConfigurationProperties(AwsS3Properties.class)
@Configuration
public class AwsS3Config {

    private final AwsS3Properties props;

    public AwsS3Config(AwsS3Properties props) {
        this.props = props;
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())
                        )
                )
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())
                        )
                )
                .build();
    }

}
