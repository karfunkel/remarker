package remarker

import groovy.beans.Bindable

class RemarkerModel {
    @Bindable String source
    @Bindable String target
    @Bindable String htmlTarget
    @Bindable String errors

    @Bindable boolean options = false

    @Bindable boolean hardwraps = true
    @Bindable boolean autoLinks = true
    @Bindable boolean abbreviations = true
    @Bindable boolean definitionLists = true
    @Bindable boolean reverseHtmlSmartQuotes = true
    @Bindable boolean reverseHtmlSmartPunctuation = true
    @Bindable boolean reverseUnicodeSmartQuotes = true
    @Bindable boolean reverseUnicodeSmartPunctuation = true
    @Bindable boolean fencedCodeBlocks = true
    @Bindable boolean tables = true
    @Bindable boolean inWordEmphasis = true
    @Bindable boolean headerIds
    @Bindable boolean inlineLinks = true
    @Bindable boolean simpleLinkIds

    @Bindable boolean fragment = true
    @Bindable boolean html
    @Bindable boolean url

    @Bindable boolean tables_codeblock
    @Bindable boolean tables_remove
    @Bindable boolean tables_extra
    @Bindable boolean tables_multi = true

    @Bindable boolean fenced_backtick
    @Bindable boolean fenced_tilde = true

    @Bindable boolean emphasis_remove = true
    @Bindable boolean emphasis_spaces

    @Bindable String find
    @Bindable String replace
    @Bindable boolean caseSensitive
    @Bindable boolean backwards
    @Bindable boolean regexp
    @Bindable boolean wholeWord
}