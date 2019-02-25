package uk.gov.ida.saml.msa.test.domain;

import stubidp.saml.utils.core.domain.IdaStatus;

public enum UnknownUserCreationIdaStatus implements IdaStatus {
    Success,
    CreateFailure,
    NoAttributeFailure,
}
