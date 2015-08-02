package jp.co.sbro.stub.comment;

import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import jp.co.sbro.stub.account.Account;
import jp.co.sbro.stub.entity.ParentGetter;

/**
 *
 * @author suzuki_yuu
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Comment {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String key;

	@Persistent
	private Account account;

	@Persistent
	private String text;

	@Persistent
	private Date postDate;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@ParentGetter
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

}
