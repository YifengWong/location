package location.web.controller;

import java.io.OutputStream;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import location.socket.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import location.util.ResultObject;
import location.util.config.StaticString;
import location.web.services.ImageService;


@Controller
public class LocationController {
	
	@Autowired  
	@Qualifier("imageService")
	ImageService imageService;
	
	
	@RequestMapping(value = "/testPost", method = RequestMethod.GET)
	public String testPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		return "testPost";
	}
	
	@RequestMapping(value = "/urlPage", method = RequestMethod.GET)
	public String urlPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		return "urlPage";
	}
	
	@RequestMapping(value = "/getuserUuid", method = RequestMethod.GET)
	public void getuserUuid(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		String str = UUID.randomUUID().toString();
		str = str.replace("-", "");
		response.getWriter().write(new ResultObject(
				StaticString.RESULT_SUCC, StaticString.UUID_GET_SUCC, str).getJsonString());
	}
	
	@RequestMapping(value="/requestPost", method=RequestMethod.POST)
	public void requestPost(@RequestParam(value="img")MultipartFile img, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/json;charset=UTF-8");
		String userUuid = request.getParameter("userUuid");

		Integer num = Integer.valueOf(request.getParameter("num"));

		String[] str0 = request.getParameter("LINEAR_ACCELERATION").split(" ");
		String[] str1 = request.getParameter("ACCELEROMETER").split(" ");
		String[] str2 = request.getParameter("GRAVITY").split(" ");
		String[] str3 = request.getParameter("ORIENTAION").split(" ");
		String[] str4 = request.getParameter("GYROSCOPE").split(" ");
		String[] str5 = request.getParameter("MAGNETIC_FIELD").split(" ");
		String[] str6 = request.getParameter("PRESSURE").split(" ");

		Float[] params = new Float[Message.PARAM_NUM * Message.PARAM_NUM_EACH];
		for (int i = 0; i < 3; i++) {
			params[0*3+i] = Float.valueOf(str0[i]);
			params[1*3+i] = Float.valueOf(str1[i]);
			params[2*3+i] = Float.valueOf(str2[i]);
			params[3*3+i] = Float.valueOf(str3[i]);
			params[4*3+i] = Float.valueOf(str4[i]);
			params[5*3+i] = Float.valueOf(str5[i]);
			params[6*3+i] = Float.valueOf(str6[i]);
		}
		
		response.getWriter().write(imageService.sendRequest(img, userUuid, num, params).getJsonString());
	}

	@RequestMapping(value="/uploadImg", method=RequestMethod.POST)
	public void uploadImg(@RequestParam(value="img")MultipartFile img, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/json;charset=UTF-8");
		String userUuid = request.getParameter("userUuid");

		ResultObject uploadRe = imageService.uploadImg(img, userUuid);
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
		out.write(imageService.getImageBytes(userUuid, imgId));
		out.flush();
		
	}	
	
	
}