var login = {};

login.AccountView = Backbone.View.extend({
	events : {
		'click #btn-login' : 'login',
		'click #btn-make-account' : 'makeAccount',
	},
	initialize : function() {
	},
	render : function() {
	},
	login : function() {
		var param = {
			'userId' : $('#user-id').val(),
			'password' : $('#password').val(),
		};
		this.model.fetch({
			data : param,
		}).done(_.bind(function() {
			auth.createSession(this.model.id);
			location.href = 'main.html';
		}, this)).fail(_.bind(function(res) {
			if (res.status === 404) {
				alert('The user id or password you entered is incorrect.');
			} else {
				console.log(res.responseText);
			}
		}, this));
	},
	makeAccount : function() {
		var $makeUserId = $('#make-user-id');
		var $makePassword = $('#make-password');
		var account = new models.Account({
			'userId' : $makeUserId.val(),
			'password' : $makePassword.val(),
		});
		account.save().done(_.bind(function() {
			alert('Succeeded!!');
			$makeUserId.val('');
			$makePassword.val('');
		}, this)).fail(function(res) {
			if (res.status === 409) {
				alert('The user id is already in used.');
			} else {
				console.log(res.responseText);
			}
		});
	},
});

login.init = function() {
	auth.invalidate();
	var account = new models.Account();
	var accountView = new login.AccountView({
		el : $('#account-view'),
		model : account,
	});
}

$(document).ready(function() {
	login.init();
});