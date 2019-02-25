package uk.gov.ida.saml.idp.core.domain;

import stubidp.saml.utils.core.domain.IdaStatus;

public enum UnknownUserCreationIdaStatus implements IdaStatus {
    Success,
    CreateFailure,
    NoAttributeFailure,
}
