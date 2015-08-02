var main = {};

main.mainRouter = null;

main.MainRouter = Backbone.Router.extend({
	routes : {
		'' : 'newComment',
		'edit-comment/:key' : 'editComment',
	},
	initialize : function(options) {
		this.me = options.me;
		this.collection = new models.Comments();
		this.headerView = new main.HeaderView({
			el : $('#header-view'),
			model : this.me,
		});
		this.postView = new main.PostView({
			el : $('#post-view'),
			collection : this.collection,
			me : this.me,
		});
		this.commentsView = new main.CommentsView({
			el : $('#comments-view'),
			collection : this.collection,
			me : this.me,
		});
	},
	newComment : function() {
		this.postView.model = new models.Comment(null, {
			collection : this.collection,
		});
		this.postView.btnCaption = 'post comment';
		this.postView.render();
	},
	editComment : function(key) {
		this.postView.model = this.collection.get(key);
		if (this.postView.model) {
			this.postView.btnCaption = 'edit comment';
			this.postView.render();
		} else {
			main.mainRouter.navigate('', {
				trigger : true,
			});
		}
	}
});

main.HeaderView = Backbone.View.extend({
	events : {
		'click #btn-change-password' : 'changePassword',
		'click #btn-delete-account' : 'deleteAccount',
	},
	initialize : function() {
		this.$password = $('#change-password');
		this.render();
	},
	render : function() {
		$('#my-info').text('hello!! ' + this.model.get('userId'));
		this.$password.val(this.model.get('password'));
	},
	changePassword : function() {
		var pre = this.model.get('password');
		this.model.set('password', this.$password.val());
		this.model.save().done(_.bind(function() {
			var post = this.model.get('password');
			alert('Succeeded!! [' + pre + ' -> ' + post + ']');
			$('#div-modal').modal('hide');
		}, this));
	},
	deleteAccount : function() {
		if (window.confirm('delete account ok?')) {
			this.model.destroy().done(_.bind(function() {
				auth.invalidate();
				location.href = 'login.html';
			}, this));
		}
	},
});

main.PostView = Backbone.View.extend({
	events : {
		'click #btn-post-comment' : 'postComment',
	},
	initialize : function(options) {
		this.me = options.me;
		this.$text = $('#text');
	},
	render : function() {
		this.$text.val(this.model.get('text'));
		$('#btn-post-comment').text(this.btnCaption);
	},
	postComment : function() {
		this.model.save({
			'myKey' : this.me.id,
			'text' : this.$text.val(),
		}).done(_.bind(function() {
			this.collection.add(this.model, {
				merge : true,
			});
			Backbone.history.fragment = '_';
			main.mainRouter.navigate('', {
				trigger : true,
			});
		}, this));
	},
});

main.CommentsView = Backbone.View.extend({
	initialize : function(options) {
		this.me = options.me;
		this.listenTo(this.collection, 'add', this.addCommentView);
		this.collection.fetch({
			silent : true,
		}).done(_.bind(function() {
			this.render();
		}, this));
	},
	render : function() {
		this.collection.each(function(comment) {
			this.addCommentView(comment);
		}, this);
		return this;
	},
	addCommentView : function(comment) {
		this.$el.prepend(new main.CommentView({
			model : comment,
			me : this.me,
		}).render().el);
	},
});

main.CommentView = Backbone.View.extend({
	tmpl : _.template($('#tmpl-comment-view').html()),
	events : {
		'click .btn-edit-comment' : 'editComment',
		'click .btn-delete-comment' : 'deleteComment',
	},
	initialize : function(options) {
		this.me = options.me;
		this.owner = this.model.get('account');
		this.model.unset('account');
		this.listenTo(this.model, 'change', this.render);
		this.listenTo(this.model, 'destroy', this.onDestroy);
	},
	render : function() {
		var json = this.model.toJSON();
		json.myComment = this.me.id === this.owner.key;
		json.ownerId = this.owner.userId;
		this.$el.html(this.tmpl(json));
		return this;
	},
	editComment : function() {
		main.mainRouter.navigate('edit-comment/' + this.model.id, {
			trigger : true,
		});
	},
	deleteComment : function() {
		this.model.destroy();
	},
	onDestroy : function() {
		this.remove();
	},
});

main.init = function(me) {
	main.mainRouter = new main.MainRouter({
		me : me,
	});
	Backbone.history.start();
};

$(document).ready(function() {
	auth.getMe(main.init);
});