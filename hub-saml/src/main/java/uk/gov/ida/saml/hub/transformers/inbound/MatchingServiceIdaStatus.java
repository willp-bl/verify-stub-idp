package uk.gov.ida.saml.hub.transformers.inbound;

import stubidp.saml.utils.core.domain.IdaStatus;

public enum MatchingServiceIdaStatus implements IdaStatus {
    NoMatchingServiceMatchFromMatchingService,
    RequesterError,
    MatchingServiceMatch,
    UserAccountCreated,
    UserAccountCreationFailed,
    Healthy
    }
