module com.udacity.catpoint.image {

    // Public API
    exports com.udacity.catpoint.image;

    // Logging
    requires org.slf4j;

    // AWS SDK v2
    requires software.amazon.awssdk.auth;
    requires software.amazon.awssdk.core;
    requires software.amazon.awssdk.regions;
    requires software.amazon.awssdk.services.rekognition;

    requires transitive java.desktop;
}
