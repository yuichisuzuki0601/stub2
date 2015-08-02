package jp.co.sbro.stub.comment;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.co.sbro.stub.DataStoreManager;
import jp.co.sbro.stub.account.Account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
*
* @author suzuki_yuu
*
*/
@Controller
@RequestMapping("/comment")
public class CommentController {

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Comment post(@RequestBody Map<String, String> param) {
		Comment comment = new Comment();
		comment.setText(param.get("text"));
		comment.setPostDate(new Date());
		Key parentKey = KeyFactory.stringToKey(param.get("myKey"));
		return DataStoreManager.createChild(Account.class, parentKey, "comments", comment);
	}

	@RequestMapping(value = "/{key}", method = RequestMethod.PUT)
	@ResponseBody
	public Comment put(@PathVariable("key") String strKey, @RequestBody Map<String, String> param) {
		Key key = KeyFactory.stringToKey(strKey);
		Map<String, Object> values = new HashMap<>();
		values.put("text", param.get("text"));
		return DataStoreManager.update(Comment.class, key, values);
	}

	@RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
	@ResponseBody
	public Comment delete(@PathVariable("key") String strKey) {
		Key key = KeyFactory.stringToKey(strKey);
		DataStoreManager.delete(Comment.class, key);
		return new Comment();
	}

}
