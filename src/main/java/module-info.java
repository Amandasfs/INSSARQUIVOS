module inss.gca.mogi.mogi {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires jbcrypt;
    requires java.desktop;

    // O "opens" é necessário para permitir que o JavaFX leia e manipule as classes de controladores por reflexão.
    opens inss.gca.mogi.mogi.controller to javafx.fxml;
    opens inss.gca.mogi.mogi.dto to javafx.base;
    opens inss.gca.mogi.mogi.model to javafx.base;
    opens inss.gca.mogi.mogi.controller.profile to javafx.fxml;
    opens inss.gca.mogi.mogi.controller.select to javafx.fxml;
    opens inss.gca.mogi.mogi.controller.create to javafx.fxml;
    opens inss.gca.mogi.mogi.controller.update to javafx.fxml;
    opens inss.gca.mogi.mogi.controller.delete to javafx.fxml;
    //exporta programa
    exports inss.gca.mogi.mogi;
}