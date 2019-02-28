package stubidp.eidas.metadata.support.builders;

import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.Conditions;
import stubidp.eidas.metadata.support.TestSamlObjectFactory;
import stubidp.test.devpki.TestEntityIds;

import java.util.ArrayList;
import java.util.List;

public class ConditionsBuilder {

    private TestSamlObjectFactory testSamlObjectFactory = new TestSamlObjectFactory();
    private List<AudienceRestriction> audienceRestrictions = new ArrayList<>();
    private AudienceRestriction defaultAudienceRestriction = testSamlObjectFactory.createAudienceRestriction(TestEntityIds.HUB_ENTITY_ID);
    private boolean shouldIncludeDefaultAudienceRestriction = true;

    public static ConditionsBuilder aConditions() {
        return new ConditionsBuilder();
    }

    public Conditions build() {
        Conditions conditions = testSamlObjectFactory.createConditions();

        if (shouldIncludeDefaultAudienceRestriction) {
            audienceRestrictions.add(defaultAudienceRestriction);
        }
        conditions.getAudienceRestrictions().addAll(audienceRestrictions);

        return conditions;
    }
}
