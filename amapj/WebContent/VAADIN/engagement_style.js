/**
 * Copyright (c) 2003-2016, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

// This file contains style definitions that can be used by CKEditor plugins.
//
// The most common use for it is the "stylescombo" plugin, which shows a combo
// in the editor toolbar, containing all styles. Other plugins instead, like
// the div plugin, use a subset of the styles on their feature.
//
// If you don't have plugins that depend on this file, you can simply ignore it.
// Otherwise it is strongly recommended to customize this file to match your
// website requirements and design properly.

CKEDITOR.stylesSet.add( 'engagement_style', [
	/* Block Styles */

	
	{ name: 'Espacement : 0pt', element: 'p', styles: { margin: '0pt 0pt 0pt 0pt' } },

	{ name: 'Espacement : 1pt', element: 'p', styles: { margin: '1pt 0pt 1pt 0pt' } },
	
	{ name: 'Espacement : 2pt', element: 'p', styles: { margin: '2pt 0pt 2pt 0pt' } },
	
	{ name: 'Espacement : 3pt', element: 'p', styles: { margin: '3pt 0pt 3pt 0pt' } },
	
	{ name: 'Espacement : 4pt', element: 'p', styles: { margin: '4pt 0pt 4pt 0pt' } },
	
	{ name: 'Espacement : 5pt', element: 'p', styles: { margin: '5pt 0pt 5pt 0pt' } },
	
	{ name: 'Espacement : 6pt', element: 'p', styles: { margin: '6pt 0pt 6pt 0pt' } },
	
	{ name: 'Espacement : 7pt', element: 'p', styles: { margin: '7pt 0pt 7pt 0pt' } },
	
	{ name: 'Espacement : 8pt', element: 'p', styles: { margin: '8pt 0pt 8pt 0pt' } },
	
	{ name: 'Espacement : 9pt', element: 'p', styles: { margin: '9pt 0pt 9pt 0pt' } },
	
	{ name: 'Espacement : 10pt', element: 'p', styles: { margin: '10pt 0pt 10pt 0pt' } },
	
	
	{ name: 'Hauteur de ligne : 1pt', element: 'p', styles: { 'line-height': '1pt' , margin: '0pt 0pt 0pt 0pt'} },
	
	{ name: 'Hauteur de ligne : 2pt', element: 'p', styles: { 'line-height': '2pt' , margin: '0pt 0pt 0pt 0pt'} },
	
	{ name: 'Hauteur de ligne : 3pt', element: 'p', styles: { 'line-height': '3pt' , margin: '0pt 0pt 0pt 0pt'} },
	
	{ name: 'Hauteur de ligne : 4pt', element: 'p', styles: { 'line-height': '4pt' , margin: '0pt 0pt 0pt 0pt'} },
	
	{ name: 'Hauteur de ligne : 5pt', element: 'p', styles: { 'line-height': '5pt' , margin: '0pt 0pt 0pt 0pt'} },
	
	{ name: 'Hauteur de ligne : 6pt', element: 'p', styles: { 'line-height': '6pt' , margin: '0pt 0pt 0pt 0pt'} },

	{ name: 'Hauteur de ligne : 7pt', element: 'p', styles: { 'line-height': '7pt' , margin: '0pt 0pt 0pt 0pt'} },
	
	{ name: 'Hauteur de ligne : 8pt', element: 'p', styles: { 'line-height': '8pt' , margin: '0pt 0pt 0pt 0pt'} },
	
	{ name: 'Hauteur de ligne : 9pt', element: 'p', styles: { 'line-height': '9pt' , margin: '0pt 0pt 0pt 0pt'} },
	
	{ name: 'Hauteur de ligne : 10pt', element: 'p', styles: { 'line-height': '10pt' , margin: '0pt 0pt 0pt 0pt'} },
	
	{ name: 'Hauteur de ligne : 11pt', element: 'p', styles: { 'line-height': '11pt' , margin: '0pt 0pt 0pt 0pt'} },
	
	{ name: 'Hauteur de ligne : 12pt', element: 'p', styles: { 'line-height': '12pt' , margin: '0pt 0pt 0pt 0pt'} },
	
	{ name: 'Hauteur de ligne : 13pt', element: 'p', styles: { 'line-height': '13pt' , margin: '0pt 0pt 0pt 0pt'} },

	
] );



CKEDITOR.on( 'dialogDefinition', function( ev ) {
    var dialogName = ev.data.name;
    var dialogDefinition = ev.data.definition;

    if ( dialogName == 'table' ) {
        var info = dialogDefinition.getContents( 'info' );

        info.get( 'txtWidth' )[ 'default' ] = '100%';       // Set default width to 100%
        info.get( 'txtBorder' )[ 'default' ] = '0';         // Set default border to 0
        info.get( 'txtCellSpace' )[ 'default' ] = '0';         // Espace entre cellule a 0 par défaut 
        
    }
});


/*
 * Remove &nbsp; entities which were inserted ie. when removing a space and
 * immediately inputting a space.
 *
 * NB: We could also set config.basicEntities to false, but this is stongly
 * adviced against since this also does not turn ie. < into &lt;.
 * @link http://stackoverflow.com/a/16468264/328272
 *
 * Based on StackOverflow answer.
 * @link http://stackoverflow.com/a/14549010/328272
 * 
 * Ceci pourra etre supprimé quand le bug 
 * http://dev.ckeditor.com/ticket/11415
 * sera corrigé 
 * 
 * A noter : la correction n'apparait que quand on quitte l'editeur ou quand 
 * on passe en source et on revient ,donc ce n'est pas completement bon 
 * 
 */
CKEDITOR.plugins.add('removeRedundantNBSP', {
  afterInit: function(editor) {
    var config = editor.config,
      dataProcessor = editor.dataProcessor,
      htmlFilter = dataProcessor && dataProcessor.htmlFilter;

    if (htmlFilter) {
      htmlFilter.addRules({
        text: function(text) {
          return text.replace(/(\w)&nbsp;/g, '$1 ');
        }
      }, {
        applyToAll: true,
        excludeNestedEditable: true
      });
    }
  }
});

