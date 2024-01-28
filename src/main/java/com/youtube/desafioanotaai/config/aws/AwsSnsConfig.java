package com.youtube.desafioanotaai.config.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.Topic;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AwsSnsConfig {
    @Getter
    private static String region;

    @Getter
    private static String accessKeyId;

    @Getter
    private static String secretKey;

    @Getter
    private static String catalogTopicArn;

    @Bean
    @Profile("test")
    public AmazonSNS amazonSNSBuilder(){
        BasicAWSCredentials credentials = new BasicAWSCredentials(getAccessKeyId(), getSecretKey());

        return AmazonSNSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(getRegion())
                .build();
    }

    @Bean
    @Profile("dev")
    public static AmazonSNS amazonSNSBuilderDev(){
        return AmazonSNSClient.builder()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        "http://localhost:4566", getRegion()))
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(getAccessKeyId(), getSecretKey())))
                .build();
    }

    @Bean(name = "catalogEventsTopic")
    public Topic snsCatalogTopicBuilder(){
        return new Topic().withTopicArn(getCatalogTopicArn());
    }

    @Value("${aws.region}")
    public void setRegion(String value) {
        region = value;
    }

    @Value("${aws.accessKeyId}")
    public void setAccessKeyId(String value) {
        accessKeyId = value;
    }

    @Value("${aws.secretKey}")
    public void setSecretKey(String value) {
        secretKey = value;
    }

    @Value("${aws.sns.topic.catalog.arn}")
    public void setCatalogTopicArn(String value) {
        catalogTopicArn = value;
    }
}
