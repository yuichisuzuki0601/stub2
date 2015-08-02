var auth = {};

auth.ACCOUNT_KEY = 'account-key';

auth.createSession = function(key) {
	localStorage.setItem(auth.ACCOUNT_KEY, key);
};

auth.getMe = function(callBack) {
	var me = new models.Account({
		'key' : localStorage.getItem(auth.ACCOUNT_KEY),
	});
	me.fetch().fail(function() {
		alert('authenticate failed.');
		location.href = 'login.html';
	}).done(function() {
		callBack(me);
	});
};

auth.invalidate = function() {
	localStorage.removeItem(auth.ACCOUNT_KEY);
};