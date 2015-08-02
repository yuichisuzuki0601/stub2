var models = {};

models.Account = Backbone.Model.extend({
	urlRoot : '/account',
	idAttribute : 'key',
});

models.Comment = Backbone.Model.extend({
	urlRoot : '/comment',
	idAttribute : 'key',
	parse : function(json) {
		json.postDateStr = new Date(json.postDate).toLocaleString();
		return json;
	}
});

models.Comments = Backbone.Collection.extend({
	model : models.Comment,
	url : '/comments',
});
