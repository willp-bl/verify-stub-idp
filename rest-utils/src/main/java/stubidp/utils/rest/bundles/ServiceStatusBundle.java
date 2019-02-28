package stubidp.utils.rest.bundles;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import stubidp.utils.rest.configuration.ServiceStatus;
import stubidp.utils.rest.filters.ConnectionCloseFilter;
import stubidp.utils.rest.resources.ServiceStatusResource;
import stubidp.utils.rest.tasks.SetServiceUnavailableTask;

public class ServiceStatusBundle implements Bundle {
    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }

    @Override
    public void run(Environment environment) {
        environment.jersey().register(new ServiceStatusResource());
        environment.jersey().register(new ConnectionCloseFilter());
        environment.admin().addTask(new SetServiceUnavailableTask(ServiceStatus.getInstance()));
    }
}
