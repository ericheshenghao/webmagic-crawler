package cn.siques.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class FileDetail {
    private String id;
    private String cookie;
    private String fileName;
    private String storagePath;
    private String url;

}
