package stubidp.saml.utils.core.domain;

public enum TransactionIdaStatus implements IdaStatus {
    Success,
    RequesterError,
    NoAuthenticationContext,
    NoMatchingServiceMatchFromHub,
    AuthenticationFailed
}
