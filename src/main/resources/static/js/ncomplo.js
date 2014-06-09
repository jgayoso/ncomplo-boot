
$(function() {

    
    /*
     * =======================================
     * NAMESPACE CREATION
     * =======================================
     */
    

    if (!$.org) {
        $.org = {};
    }
    if (!$.org.jgayoso) {
        $.org.jgayoso = {};
    }
    if ($.org.jgayoso.ncomplo) {
        throw new Error('org.jgayoso.ncomplo already exists!');
    }
  
    $.org.jgayoso.ncomplo = {};
  

    
    /*
     * =======================================
     * GLOBAL VARIABLES (ncomplo-object scope)
     * =======================================
     */
    
    
    $.org.jgayoso.ncomplo.scoreboard = {};
    $.org.jgayoso.ncomplo.scoreboard.POSITION_SELECTOR = '#scoreboard tbody tr td.scoreboard_position';    
    $.org.jgayoso.ncomplo.scoreboard.POSITION_CLASS_PREFIX = 'scoreboard_pos';
    
    $.org.jgayoso.ncomplo.lang = {};
    $.org.jgayoso.ncomplo.lang.prototypes = {};
    $.org.jgayoso.ncomplo.lang.LANG_INPUT_CLASS = 'langInput';
    $.org.jgayoso.ncomplo.lang.VALUE_INPUT_CLASS = 'valueInput';
    $.org.jgayoso.ncomplo.lang.LANG_BLOCK_CLASS_SUFFIX = 'lang-element';
    $.org.jgayoso.ncomplo.lang.LANG_BLOCK_NEW_CLASS_SUFFIX = '-lang-new';
    $.org.jgayoso.ncomplo.lang.LANG_BLOCK_EXISTING_CLASS_SUFFIX = '-lang-existing';
    $.org.jgayoso.ncomplo.lang.LANG_BLOCK_PROTOTYPE_ID_SUFFIX = '-lang-prototype';
        
    
    
    /*
     * =======================================
     * INITIALIZATION
     * =======================================
     */
    
    
    $.org.jgayoso.ncomplo.initialize =
        function() {
            
            var ncomplo = this;

            /*
             * --------------------------
             * Initialize scoreboard
             * --------------------------
             */
            
            this.scoreboard.removeRepeatedPositions();

            
            
            /*
             * --------------------------
             * Initialize lang prototypes
             * --------------------------
             */
        
            // Retrieves all lang-oriented prototype divs, which
            // "id" attribute should end with "-lang"
            var prototypes = $('[id$=' + ncomplo.lang.LANG_BLOCK_PROTOTYPE_ID_SUFFIX + ']');
            
            prototypes.each(
                    function() {
                        var prototype = this;
                        ncomplo.lang.prototypes[this.id] = 
                            {
                                name : 
                                    prototype.id.substring(0,this.id.indexOf(ncomplo.lang.LANG_BLOCK_PROTOTYPE_ID_SUFFIX)),
                                index : 
                                    (function() {
                                        var name = 
                                            prototype.id.substring(0,prototype.id.indexOf(ncomplo.lang.LANG_BLOCK_PROTOTYPE_ID_SUFFIX));
                                        var existingClass =
                                            name + ncomplo.lang.LANG_BLOCK_EXISTING_CLASS_SUFFIX;
                                        var existingElements = 
                                            $(prototype).parent().children().filter('.' + existingClass);
                                        existingElements.each(
                                                function() {
                                                    $(this).addClass(this.LANG_BLOCK_CLASS_SUFFIX);
                                                });
                                        return existingElements.size();
                                    })(),
                                element: $(this)
                            };
                        $(this).remove();
                    });

            
        };
        
        
        
    /*
     * =======================================
     * LANG
     * =======================================
     */
        
        
    $.org.jgayoso.ncomplo.scoreboard.removeRepeatedPositions =
        function(langElementId) {
        
            var scoreboard = this;
            
            var scoreboardEntries = $(scoreboard.POSITION_SELECTOR);
            
            if (scoreboardEntries != null) {
            
                (function () {
                    var lastPos = -1;
                    scoreboardEntries.each(
                        function() {
                            var posElement = $(this);
                            var posSpan = posElement.find('span');
                            var pos = parseInt(posSpan.html());
                            if (lastPos != pos) {
                                lastPos = pos;
                            } else {
                                posSpan.html('');
                            }
                            posElement.parent().addClass(scoreboard.POSITION_CLASS_PREFIX + pos);
                        });
                })();
                
            }
            
        };
        
            
        
        
        
    /*
     * =======================================
     * LANG
     * =======================================
     */
        
        
    $.org.jgayoso.ncomplo.lang.add =
        function(langElementId) {
        
            var defaultLangElement = $('#'+langElementId);
            var prototypeObject = this.prototypes[langElementId + this.LANG_BLOCK_PROTOTYPE_ID_SUFFIX];
            
            var newLangElement = prototypeObject.element.clone();
            newLangElement.attr('id',null);
            newLangElement.addClass(prototypeObject.name + this.LANG_BLOCK_NEW_CLASS_SUFFIX);
            newLangElement.addClass(this.LANG_BLOCK_CLASS_SUFFIX);
            
            var langInput = newLangElement.children().filter('.' + this.LANG_INPUT_CLASS);
            var valueInput = newLangElement.children().filter('.' + this.VALUE_INPUT_CLASS);

            langInput.attr(
                    'name',
                    langInput.attr('name').replace('$index$',prototypeObject.index));
            valueInput.attr(
                    'name',
                    valueInput.attr('name').replace('$index$',prototypeObject.index));

            langInput.attr(
                    'id',
                    langInput.attr('id').replace('$index$',prototypeObject.index));
            valueInput.attr(
                    'id',
                    valueInput.attr('id').replace('$index$',prototypeObject.index));
            
            prototypeObject.index++;

            
            var existingElements =
                defaultLangElement.parent().children().filter('.' + prototypeObject.name + this.LANG_BLOCK_EXISTING_CLASS_SUFFIX);
            var newElements =
                defaultLangElement.parent().children().filter('.' + prototypeObject.name + this.LANG_BLOCK_NEW_CLASS_SUFFIX);

            if (newElements.size() > 0) {
                newElements.last().after(newLangElement);
            } else if (existingElements.size() > 0) {
                existingElements.last().after(newLangElement);
            } else {
                defaultLangElement.after(newLangElement);
            }
            
        };
        
        
        
        $.org.jgayoso.ncomplo.lang.remove =
            function(removalLinkElement) {
                var langParent = $(removalLinkElement);
                while (!langParent.hasClass(this.LANG_BLOCK_CLASS_SUFFIX)) {
                    langParent = langParent.parent();
                }
            
                langParent.remove();
            };
            
            
            
            
    /*
     * =======================================
     * INITIALIZATION
     * =======================================
     */
        
    $.org.jgayoso.ncomplo.initialize();
    
    
});

