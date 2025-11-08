package os.org.filley.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Route;
import os.org.filley.entity.FileData;
import os.org.filley.service.FilleyService;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

@Route("")
public class FilleyView extends VerticalLayout {

    private final FilleyService fileService;
    private final Grid<FileData> grid;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public FilleyView(FilleyService fileService) {
        this.fileService = fileService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("min-height", "100vh");

        // Header
        Div header = createHeader();

        // Content Container
        VerticalLayout contentContainer = new VerticalLayout();
        contentContainer.setWidth("95%");
        contentContainer.setMaxWidth("1200px");
        contentContainer.getStyle()
                .set("margin", "0 auto")
                .set("background", "white")
                .set("border-radius", "20px")
                .set("box-shadow", "0 20px 60px rgba(0,0,0,0.3)")
                .set("padding", "30px");

        // Upload Section
        Div uploadSection = createUploadSection();

        // Grid
        grid = createGrid();

        contentContainer.add(uploadSection, grid);

        add(header, contentContainer);
        setHorizontalComponentAlignment(Alignment.CENTER, contentContainer);

        refreshGrid();
    }

    private Div createHeader() {
        Div header = new Div();
        header.getStyle()
                .set("width", "100%")
                .set("padding", "30px")
                .set("text-align", "center");

        H1 title = new H1("📱 File Share Hub");
        title.getStyle()
                .set("color", "white")
                .set("margin", "0")
                .set("font-weight", "700")
                .set("font-size", "2.5rem")
                .set("text-shadow", "2px 2px 4px rgba(0,0,0,0.2)");

        Span subtitle = new Span("Share files seamlessly between your devices");
        subtitle.getStyle()
                .set("color", "rgba(255,255,255,0.9)")
                .set("font-size", "1.1rem")
                .set("display", "block")
                .set("margin-top", "10px");

        header.add(title, subtitle);
        return header;
    }

    private Div createUploadSection() {
        Div uploadSection = new Div();
        uploadSection.getStyle()
                .set("padding", "30px")
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border-radius", "15px")
                .set("margin-bottom", "30px")
                .set("box-shadow", "0 10px 30px rgba(102, 126, 234, 0.3)");

        // Create Upload with new UploadHandler API (Vaadin 24.8+)
        Upload upload = new Upload(event -> {
            var ui = event.getUI();
            String fileName = event.getFileName();
            String contentType = event.getContentType();
            InputStream inputStream = event.getInputStream();

            try {
                // Store file directly using InputStream
                fileService.storeFile(fileName, inputStream, contentType);

                // UI updates must be done via UI.access()
                ui.access(() -> {
                    Notification notification = Notification.show(
                            "✅ " + fileName + " uploaded successfully!",
                            3000,
                            Notification.Position.TOP_CENTER
                    );
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    refreshGrid();
                });

            } catch (IOException e) {
                ui.access(() -> {
                    Notification.show("❌ Upload failed: " + e.getMessage(),
                                    3000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                });
            }
        });

        upload.setMaxFiles(10);
        upload.setDropLabel(new Span("📤 Drop files here or click to browse"));
        upload.getStyle()
                .set("width", "100%");

        uploadSection.add(upload);
        return uploadSection;
    }

    private Grid<FileData> createGrid() {
        Grid<FileData> grid = new Grid<>(FileData.class, false);
        grid.addClassName("file-grid");
        grid.getStyle()
                .set("border-radius", "10px")
                .set("overflow", "hidden");

        grid.addColumn(FileData::getFileName)
                .setHeader("📄 File Name")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(file -> fileService.formatFileSize(file.getFileSize()))
                .setHeader("📊 Size")
                .setAutoWidth(true);

        grid.addColumn(file -> file.getUploadDate().format(dateFormatter))
                .setHeader("🕐 Upload Date")
                .setAutoWidth(true);

        grid.addComponentColumn(file -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            Button downloadBtn = new Button("Download", VaadinIcon.DOWNLOAD.create());
            downloadBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            downloadBtn.getStyle()
                    .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                    .set("border", "none");

            downloadBtn.addClickListener(e -> downloadFile(file));

            Button deleteBtn = new Button("Delete", VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> deleteFile(file));

            actions.add(downloadBtn, deleteBtn);
            return actions;
        }).setHeader("⚡ Actions").setAutoWidth(true);

        return grid;
    }

    private void downloadFile(FileData file) {
        try {
            // Use REST endpoint for proper download
            String downloadUrl = "/api/files/download/" + file.getId();

            getUI().ifPresent(ui -> {
                ui.getPage().open(downloadUrl, "_blank");

                Notification.show("⬇️ Downloading " + file.getFileName(),
                        2000, Notification.Position.BOTTOM_CENTER);
            });
        } catch (Exception e) {
            Notification.show("❌ Download failed: " + e.getMessage(),
                            3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteFile(FileData file) {
        try {
            fileService.deleteFile(file.getId());
            Notification.show("🗑️ " + file.getFileName() + " deleted!",
                            2000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            refreshGrid();
        } catch (IOException e) {
            Notification.show("❌ Delete failed: " + e.getMessage(),
                            3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void refreshGrid() {
        grid.setItems(fileService.getAllFiles());
    }
}