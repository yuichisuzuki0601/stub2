package jp.co.sbro.stub.comment;

import java.util.List;

import jp.co.sbro.stub.DataStoreManager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
*
* @author suzuki_yuu
*
*/
@Controller
@RequestMapping("/comments")
public class CommentsController {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Comment> get() {
		return DataStoreManager.read(Comment.class, "postDate");
	}

}
