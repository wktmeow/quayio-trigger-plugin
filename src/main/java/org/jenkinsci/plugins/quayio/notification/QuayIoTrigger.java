package org.jenkinsci.plugins.quayio.notification;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import jenkins.model.Jenkins;
import jenkins.model.ParameterizedJobMixIn;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.*;

/**
 * Created by sirot on 17/01/2016.
 */
public class QuayIoTrigger extends Trigger<Job<?, ?>> {

    private Set<String> repositories;

    @DataBoundConstructor
    public QuayIoTrigger() {
        this.repositories = Collections.emptySet();
    }

    public QuayIoTrigger(Set<String> repositories) {
        this.repositories = repositories;
    }

    @DataBoundSetter
    public void setRepositories(Set<String> repositories) {
        this.repositories = repositories;
    }

    public Set<String> getRepositories() {
        return repositories;
    }

    public String getRepositoriesAsMultiline() {
        return StringUtils.join(getRepositories(), "\n");
    }

    public static QuayIoTrigger getTrigger(ParameterizedJobMixIn.ParameterizedJob job) {
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins != null) {
            QuayIoTrigger.DescriptorImpl descriptor = jenkins.getDescriptorByType(QuayIoTrigger.DescriptorImpl.class);
            if (descriptor != null) {
                Map<TriggerDescriptor, Trigger<?>> triggers = job.getTriggers();
                return (QuayIoTrigger)triggers.get(descriptor);
            }
        }
        return null;
    }

    @Extension
    public static class DescriptorImpl extends TriggerDescriptor {

        @Override
        public boolean isApplicable(Item item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Monitor Quay.io for image changes";
        }

        @Override
        public Trigger<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            JSONArray array = new JSONArray();
            if (formData.has("repositories") && !StringUtils.isBlank(formData.optString("repositories"))) {
                array.addAll(Arrays.asList(StringUtils.split(formData.getString("repositories"))));
            }
            formData.put("repositories", array);
            return super.newInstance(req, formData);
        }
    }

}
