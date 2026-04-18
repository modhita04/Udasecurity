module com.udacity.catpoint.security {

    // Project modules
    requires com.udacity.catpoint.image;

    // External libraries
    requires miglayout.swing;
    requires com.google.gson;
    requires com.google.common;
    requires org.slf4j;

    // Java platform
    requires java.desktop;
    requires java.prefs;
    requires java.sql;


    opens com.udacity.catpoint.security.data to com.google.gson;
}
