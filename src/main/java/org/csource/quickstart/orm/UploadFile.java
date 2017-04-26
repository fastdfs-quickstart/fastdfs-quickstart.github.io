package org.csource.quickstart.orm;

import lombok.Data;

@Data
public class UploadFile {

  private Long id;

  private Long size;

  private Long expiretime;

  private Long createtime;

  private String url;

  private String remark;

  private Short isdelete;

  private String filemd5;
}
