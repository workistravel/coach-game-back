package pl.dernovyi.coushgameback.service.impl;

import com.microsoft.azure.storage.AccessCondition;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StorageService {
    @Value("${endpoint}")
    private String endpoint;
    @Value("${nameLibrary}")
    private String nameLibrary;

    public URI saveToStorage(MultipartFile multipartFile) throws URISyntaxException, StorageException, IOException, InvalidKeyException {
        URI uri;
        CloudBlobContainer image = getCloudBlobContainer();

        StringBuilder name = new  StringBuilder()
                .append(UUID.randomUUID().toString())
                .append(".")
                .append(getTypeFile(multipartFile));

        CloudBlockBlob blockBlobReference = null;
            blockBlobReference = image.getBlockBlobReference(Objects.requireNonNull( name.toString()));
            blockBlobReference.upload(multipartFile.getInputStream(), -1);

        uri = blockBlobReference.getUri();
        return uri;
    }

    public void removeInStorage(String url) throws URISyntaxException, StorageException, IOException, InvalidKeyException {

        CloudBlobContainer container = getCloudBlobContainer();
        CloudBlockBlob blockBlobReference = container.getBlockBlobReference(url);
        String prefix = blockBlobReference.getParent().getPrefix();
        String name = url.replaceAll(prefix,"");
        container.getBlockBlobReference(name).deleteIfExists();
    }

    private CloudBlobContainer getCloudBlobContainer() throws URISyntaxException, InvalidKeyException, StorageException {

//        CloudBlobClient blobClient = storageAccount.createCloudBlobClient ();
//        CloudBlobContainer blobContainer = blobClient.getContainerReference ( «имя-контейнера» );
        CloudStorageAccount account = CloudStorageAccount.parse(endpoint);;

        CloudBlobClient client = account.createCloudBlobClient();

        return client.getContainerReference(nameLibrary);

    }

    private String getTypeFile(@RequestParam("file") MultipartFile multipartFile) {
        Pattern pattern = Pattern.compile("[^\\/]*$");
        Matcher matcher = pattern.matcher(multipartFile.getContentType());

        String ext = null;
        if (matcher.find()) {
            ext = matcher.group();
        }
        return ext;
    }
}
