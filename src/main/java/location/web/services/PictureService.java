package location.web.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import location.util.ResultObject;
import location.util.config.StaticString;

@Service
public class PictureService {
	// 配置文件
	private Properties config = new Properties();
	
	// 存放的文件夹
	private File saveDir = null;
	
	private PictureService() {
		super();
		try {
			config.load(this.getClass().getClassLoader().getResourceAsStream("image-config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		saveDir = new File (config.getProperty("savePicPath") + config.getProperty("saveDir"));
		if (!saveDir.exists()) saveDir.mkdirs();
	}
	
	/**
	 * @param file
	 * @return
	 * @throws Exception
	 * 上传文件
	 */
	public ResultObject uploadImg(MultipartFile file, String userUuid) throws Exception {
		String fileName = file.getOriginalFilename();
		String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
		String allowSuffixs = config.getProperty("allowPicSuffixs");
		if(allowSuffixs.indexOf(suffix) == -1) {
			return new ResultObject(StaticString.RESULT_FAIL, StaticString.IMG_WRONG_SUFFIXS, null);
		}
//		String newFileName = PictureService.getUniqueFileName()+ "." + suffix;
		File newDir = new File(saveDir.getPath() + File.separator + userUuid);
		if (!newDir.exists()) newDir.mkdirs();
		String newFileUri = newDir.getPath() + File.separator + fileName;
		
		System.out.println(newFileUri);
		System.out.println(file.getSize());
		
		
		try {
            FileOutputStream out = new FileOutputStream(newFileUri);
            // 写入文件
            out.write(file.getBytes());
            out.flush();
            out.close();
            return new ResultObject(StaticString.RESULT_SUCC, StaticString.IMG_UPLOAD_SUCC, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return new ResultObject(StaticString.RESULT_FAIL, StaticString.IMG_UPLOAD_FAIL, null);

	}
	
	/**
	 * @return
	 * 获取唯一值作为文件名
	 */
	private static String getUniqueFileName() {
		String str = UUID.randomUUID().toString();
		return str.replace("-", "");
	}
	
	/**
	 * @param name
	 * @return
	 * 返回图片文件的字节流
	 */
	public byte[] getImageBytes(String userUuid, String name) {
		File file = new File(saveDir + File.separator + userUuid + File.separator + name);
		FileInputStream fis = null;
		byte[] b = null;
		try {
			fis = new FileInputStream(file);
			b = new byte[fis.available()];
			fis.read(b);
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
			        e.printStackTrace();
			    }
			}
		}
		return b;
		
	}
	
}