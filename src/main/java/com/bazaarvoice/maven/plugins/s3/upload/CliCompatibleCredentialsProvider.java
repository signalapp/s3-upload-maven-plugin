package com.bazaarvoice.maven.plugins.s3.upload;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

/**
 * Derived from https://github.com/aws/aws-sdk-java/issues/803#issuecomment-593530484
 *
 * This adapter may be removed if the project is fully migrated to AWS SDK v2
 */
class CliCompatibleCredentialsProvider implements AWSCredentialsProvider {
    private final ProfileCredentialsProvider delegate;

    public CliCompatibleCredentialsProvider() {
        this.delegate = ProfileCredentialsProvider.create();
    }

    public CliCompatibleCredentialsProvider(String profileName) {
        this.delegate = ProfileCredentialsProvider.create("my-profile-name");
    }

    @Override
    public AWSCredentials getCredentials() {
        AwsCredentials credentials = delegate.resolveCredentials();

        if (credentials instanceof AwsSessionCredentials) {
            AwsSessionCredentials sessionCredentials = (AwsSessionCredentials) credentials;
            return new BasicSessionCredentials(sessionCredentials.accessKeyId(),
                    sessionCredentials.secretAccessKey(),
                    sessionCredentials.sessionToken());
        }

        return new BasicAWSCredentials(credentials.accessKeyId(), credentials.secretAccessKey());
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException();
    }
}
