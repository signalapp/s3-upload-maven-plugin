package com.bazaarvoice.maven.plugins.s3.upload;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.services.sso.SsoClient;
import software.amazon.awssdk.services.sso.auth.SsoCredentialsProvider;

/**
 * Derived from https://github.com/aws/aws-sdk-java/issues/2434#issuecomment-819985174
 *
 * This adapter may be removed if the project is fully migrated to AWS SDK v2
 */
class SsoCompatibleCredentialsProvider implements AWSCredentialsProvider {
    private final SsoCredentialsProvider delegate;

    public SsoCompatibleCredentialsProvider() {
        final SsoClient ssoClient = SsoClient.builder().build();
        SsoCredentialsProvider.Builder builder = SsoCredentialsProvider.builder();

        builder.ssoClient(ssoClient);

        this.delegate = builder.build();
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
