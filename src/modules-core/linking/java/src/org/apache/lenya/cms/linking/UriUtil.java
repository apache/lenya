package org.apache.lenya.cms.linking;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lenya.util.StringUtil;

public class UriUtil {

    public static String getRelativeUri(String sourceUri, String targetUri) {
        final boolean empty = targetUri.equals("");
        if (empty) {
            targetUri = "/";
        }

        final String baseUri = sourceUri.substring(0, sourceUri.lastIndexOf('/') + 1);

        final Path basePath = new Path(baseUri);
        final Path targetPath = new Path(targetUri);

        while (!basePath.isEmpty() && targetPath.size() > 1
                && basePath.first().equals(targetPath.first())) {
            basePath.removeFirst();
            targetPath.removeFirst();
        }

        String prefix = basePath.isEmpty() ? "" : generateUpDots(basePath.getSteps().size() - 1);

        String targetString = prefix + targetPath;
        if (empty) {
            if (!targetString.endsWith("/")) {
                throw new IllegalStateException("Target URI " + targetString
                        + " doesn't end with a slash!");
            }
            targetString = targetString.substring(0, targetString.length() - 1);
        }

        try {
            return new URI(targetString).normalize().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected static String getLastStep(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    protected static String generateUpDots(int length) {
        if (length == 0) {
            return "./";
        }
        final String[] upDotsArray = new String[length];
        Arrays.fill(upDotsArray, "..");
        return StringUtil.join(upDotsArray, "/") + (upDotsArray.length == 0 ? "" : "/");
    }

    protected static class Path {

        private List<String> steps = new ArrayList<String>();

        public Path(final String path) {
            if (!path.startsWith("/")) {
                throw new IllegalArgumentException("Path " + path + " must start with a slash.");
            }
            this.steps.addAll(Arrays.asList(path.substring(1).split("/", -1)));
        }

        public int size() {
            return this.steps.size();
        }

        public String removeFirst() {
            return this.steps.remove(0);
        }

        public String toString() {
            return StringUtil.join(this.steps.toArray(new String[this.steps.size()]), "/");
        }

        public String debug() {
            final StringBuffer buf = new StringBuffer();
            for (final String step : this.steps) {
                buf.append("[" + step + "]");
            }
            return buf.toString();
        }

        public List<String> getSteps() {
            return Collections.unmodifiableList(this.steps);
        }

        public boolean isEmpty() {
            return this.steps.isEmpty();
        }

        public String first() {
            return this.steps.get(0);
        }

        public String last() {
            return this.steps.get(this.steps.size() - 1);
        }

    }

}
