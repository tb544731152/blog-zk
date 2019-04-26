package com.weapp.entity.file;

import java.util.Date;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 保存文件
 * @author zk
 *
 */
@Document(collection = "img_collection")
public class TFile {

	@Id
	private String _id; // mongodb主键
	private String imgId; //生成文件Id
    private String name; // 文件名称
    private String contentType; // 文件类型
    private long size;
    private Date uploadDate;
    private Binary content; // 文件内容
    private String path; // 文件路径
    
    public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Binary getContent() {
		return content;
	}

	public void setContent(Binary content) {
		this.content = content;
	}
    
    public TFile() {
    }
    
    public TFile(String name, String contentType, long size, Binary content) {
    	this.name = name;
    	this.contentType = contentType;
    	this.size = size;
    	this.uploadDate = new Date();
    	this.content = content;
    }
   
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        TFile fileInfo = (TFile) object;
        return java.util.Objects.equals(size, fileInfo.size)
                && java.util.Objects.equals(name, fileInfo.name)
                && java.util.Objects.equals(contentType, fileInfo.contentType)
                && java.util.Objects.equals(uploadDate, fileInfo.uploadDate)
                && java.util.Objects.equals(_id, fileInfo._id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, contentType, size, uploadDate, _id);
    }

    @Override
    public String toString() {
        return "File{"
                + "name='" + name + '\''
                + ", contentType='" + contentType + '\''
                + ", size=" + size
                + ", uploadDate=" + uploadDate
                + ", _id='" + _id + '\''
                + '}';
    }

	public String getImgId() {
		return imgId;
	}

	public void setImgId(String imgId) {
		this.imgId = imgId;
	}
    
}
