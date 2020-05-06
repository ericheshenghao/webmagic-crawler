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
public class SoundFile {
    private Integer id;
    private String name ;
    private String url;
    private String ext;
    private String path;
    private String size;
    private String classification;
    private String description;
    private String location;
    private Boolean statu;
}
