package location.web.controller;

import java.io.OutputStream;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import location.util.ResultObject;
import location.util.config.StaticString;
import location.web.services.PictureService;
import location.web.services.SocketService;



@Controller
public class LocationController {
	
	@Autowired  
	@Qualifier("pictureService") 
	PictureService pictureService;
	
	@Autowired  
	@Qualifier("socketService") 
	SocketService socketService;
	
	
	@RequestMapping(value = "/testPost", method = RequestMethod.GET)
	public String testPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		return "testPost";
	}
	
	@RequestMapping(value = "/getuserUuid", method = RequestMethod.GET)
	public void getuserUuid(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		String str = UUID.randomUUID().toString();
		str = str.replace("-", "");
		response.getWriter().write(new ResultObject(
				StaticString.RESULT_SUCC, StaticString.UUID_GET_SUCC, str).getJsonString());
	}
	
	@RequestMapping(value="/uploadImg", method=RequestMethod.POST)
	public void uploadImg(@RequestParam(value="img")MultipartFile img, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/json;charset=UTF-8");
		String userUuid = request.getParameter("userUuid");
		
		ResultObject uploadRe = pictureService.uploadImg(img, userUuid);
		if (uploadRe.getObject() == null) {
			response.getWriter().write(uploadRe.getJsonString());
			return;
		}
		
		response.getWriter().write(uploadRe.getJsonString());
	}

	
	@RequestMapping(value = "/getImg", method=RequestMethod.POST)
	public void getImg(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		String userUuid = request.getParameter("userUuid");
		String imgId = request.getParameter("imgId");
		
		String suffix = imgId.substring(imgId.lastIndexOf(".")+1);
		response.setContentType("image/" + suffix);
		
		OutputStream out = response.getOutputStream();
		out.write(pictureService.getImageBytes(userUuid, imgId));
		out.flush();
		
	}
	
	
	
}