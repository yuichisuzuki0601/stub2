package jp.co.sbro.stub.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.sbro.stub.DataStoreManager;
import jp.co.sbro.stub.errorstatus.Conflict;
import jp.co.sbro.stub.errorstatus.NotFound;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
*
* @author suzuki_yuu
*
*/
@Controller
@RequestMapping("/account")
public class AccountController {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Account get(@RequestParam String userId, @RequestParam String password) {
		Map<String, Object> condition = new HashMap<>();
		condition.put("userId", userId);
		condition.put("password", password);
		List<Account> list = DataStoreManager.read(Account.class, condition);
		if (list.size() != 1) {
			throw new NotFound();
		}
		return list.get(0);
	}

	@RequestMapping(value = "/{key}", method = RequestMethod.GET)
	@ResponseBody
	public Account get(@PathVariable("key") String strKey) {
		Key key = KeyFactory.stringToKey(strKey);
		return DataStoreManager.read(Account.class, key);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Account post(@RequestBody Map<String, String> param) {
		String userId = param.get("userId");
		Map<String, Object> condition = new HashMap<>();
		condition.put("userId", userId);
		List<Account> list = DataStoreManager.read(Account.class, condition);
		if (list.size() > 0) {
			throw new Conflict();
		}
		Account account = new Account();
		account.setUserId(userId);
		account.setPassword(param.get("password"));
		return DataStoreManager.create(account);
	}

	@RequestMapping(value = "/{key}", method = RequestMethod.PUT)
	@ResponseBody
	public Account put(@PathVariable("key") String strKey, @RequestBody Map<String, String> param) {
		Key key = KeyFactory.stringToKey(strKey);
		Map<String, Object> values = new HashMap<>();
		values.put("userId", param.get("userId"));
		values.put("password", param.get("password"));
		return DataStoreManager.update(Account.class, key, values);
	}

	@RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
	@ResponseBody
	public Account delete(@PathVariable("key") String strKey) {
		Key key = KeyFactory.stringToKey(strKey);
		DataStoreManager.delete(Account.class, key);
		return new Account();
	}

}
