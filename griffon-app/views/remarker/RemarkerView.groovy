package remarker

import java.awt.Font
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.JSplitPane
import org.lobobrowser.html.UserAgentContext
import org.lobobrowser.html.gui.HtmlPanel
import org.lobobrowser.html.parser.DocumentBuilderImpl
import org.lobobrowser.html.test.SimpleHtmlRendererContext
import org.lobobrowser.html.test.SimpleUserAgentContext
import javax.swing.JFileChooser

fileChooser(id: "openFile", dialogTitle: "Choose a file", fileSelectionMode: JFileChooser.FILES_ONLY)

dialog(id: 'findDialog', title: 'Find and Replace', modal: false, alwaysOnTop: true, resizable: true, undecorated: false) {
    migLayout(layoutConstraints: 'wrap 2, fill', rowConstraints: '[][][][][][][]', columnConstraints: '[][fill, grow]')
    label('Search')
    textField(id: 'findField', text: bind(source: model, sourceProperty: 'find', mutual: true))
    label('Replace')
    textField(text: bind(source: model, sourceProperty: 'replace', mutual: true))
    label('')
    checkBox(text: 'Regular Expression', selected: bind(source: model, sourceProperty: 'regexp', mutual: true))
    label('')
    checkBox(text: 'Whole word', selected: bind(source: model, sourceProperty: 'wholeWord', mutual: true))
    label('')
    checkBox(text: 'Case sensitive', selected: bind(source: model, sourceProperty: 'caseSensitive', mutual: true))
    label('')
    checkBox(text: 'Backwards', selected: bind(source: model, sourceProperty: 'backwards', mutual: true))
    panel(constraints: 'spanx 3') {
        migLayout(layoutConstraints: 'fill', rowConstraints: '[fill, grow]', columnConstraints: '[fill, grow]')
        button(text: 'Replace All', actionPerformed: controller.&replaceAll, constraints: 'left')
        button(text: 'Replace', actionPerformed: controller.&replace, constraints: 'center')
        button(text: 'Find', actionPerformed: controller.&searchText, defaultButton: true, constraints: 'right')
    }
}

def css = RemarkerView.classLoader.getResource('markdown.css').text

application(title: 'remarker',
        preferredSize: [1280, 768],
        pack: true,
        //location: [50,50],
        locationByPlatform: true,
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]) {
    splitPane(orientation: JSplitPane.VERTICAL_SPLIT, dividerLocation: 650) {
        splitPane(orientation: JSplitPane.HORIZONTAL_SPLIT, dividerLocation: 710) {
            splitPane(orientation: JSplitPane.VERTICAL_SPLIT, dividerLocation: 320) {
                panel {
                    migLayout(layoutConstraints: 'wrap 1, fill, hidemode 3', rowConstraints: '[][fill, grow][]', columnConstraints: '[fill, grow]')
                    panel {
                        migLayout(layoutConstraints: '', rowConstraints: '[fill,grow]', columnConstraints: '[][][fill, grow][]0')
                        buttonGroup().with { group ->
                            radioButton(text: 'HTML Fragment', selected: bind(source: model, sourceProperty: 'fragment', mutual: true), buttonGroup: group)
                            radioButton(text: 'Full HTML', selected: bind(source: model, sourceProperty: 'html', mutual: true), buttonGroup: group)
                            //radioButton(text: 'File', selected: bind(source: model, sourceProperty: 'file', mutual: true), buttonGroup: group)
                            radioButton(text: 'URL', selected: bind(source: model, sourceProperty: 'url', mutual: true), buttonGroup: group)
                        }
                        label('')
                        button('Load', actionPerformed: controller.&loadHTML, constraints: 'right')
                    }
                    scrollPane {
                        textArea(id: 'source', text: bind(source: model, sourceProperty: 'source', mutual: true), font: new Font('Monospaced', Font.BOLD, 12))
                    }
                    panel(id: 'nooptions', visible: bind { !model.options }) {
                        migLayout(layoutConstraints: 'fill', rowConstraints: '[]', columnConstraints: '[fill,grow][fill,grow][fill,grow]')
                        checkBox(text: 'Show options', selected: bind(source: model, sourceProperty: 'options', mutual: true), constraints: 'top, left')
                        label('')
                        button(text: 'Convert', actionPerformed: controller.&convert, constraints: 'h 60, grow')
                    }
                    panel(id: 'options', visible: bind { model.options }) {
                        migLayout(layoutConstraints: 'wrap 6, fill, flowy', rowConstraints: '[fill,grow]', columnConstraints: '[fill,grow][fill,grow][fill,grow]')
                        checkBox(text: 'Show options', selected: bind(source: model, sourceProperty: 'options', mutual: true))
                        checkBox(text: 'Hardwraps', selected: bind(source: model, sourceProperty: 'hardwraps', mutual: true))
                        checkBox(text: 'Auto Links', selected: bind(source: model, sourceProperty: 'autoLinks', mutual: true))
                        checkBox(text: 'Abbreviations', selected: bind(source: model, sourceProperty: 'abbreviations', mutual: true))
                        checkBox(text: 'Definition Lists', selected: bind(source: model, sourceProperty: 'definitionLists', mutual: true))
                        panel {
                            migLayout(layoutConstraints: 'wrap 1', rowConstraints: '5[]10[]', columnConstraints: '0[]')
                            checkBox(text: 'Simple Link Ids', selected: bind(source: model, sourceProperty: 'simpleLinkIds', mutual: true))
                            checkBox(text: 'Inline Links', selected: bind(source: model, sourceProperty: 'inlineLinks', mutual: true))

                        }
                        checkBox(text: 'Fenced Code Blocks', selected: bind(source: model, sourceProperty: 'fencedCodeBlocks', mutual: true))
                        panel() {
                            migLayout(layoutConstraints: 'wrap 2')
                            buttonGroup().with { group ->
                                radioButton(text: 'Backtick', selected: bind(source: model, sourceProperty: 'fenced_backtick', mutual: true), buttonGroup: group, enabled: bind { model.fencedCodeBlocks })
                                radioButton(text: 'Tilde', selected: bind(source: model, sourceProperty: 'fenced_tilde', mutual: true), buttonGroup: group, enabled: bind { model.fencedCodeBlocks })
                            }
                        }
                        checkBox(text: 'In Word Emphasis', selected: bind(source: model, sourceProperty: 'inWordEmphasis', mutual: true))
                        panel() {
                            migLayout(layoutConstraints: 'wrap 2')
                            buttonGroup().with { group ->
                                radioButton(text: 'Remove emphasis', selected: bind(source: model, sourceProperty: 'emphasis_remove', mutual: true), buttonGroup: group, enabled: bind { model.inWordEmphasis })
                                radioButton(text: 'Add spaces', selected: bind(source: model, sourceProperty: 'emphasis_spaces', mutual: true), buttonGroup: group, enabled: bind { model.inWordEmphasis })
                            }
                        }
                        checkBox(text: 'Tables', selected: bind(source: model, sourceProperty: 'tables', mutual: true))
                        panel() {
                            migLayout(layoutConstraints: 'wrap 2')
                            buttonGroup().with { group ->
                                radioButton(text: 'Remove', selected: bind(source: model, sourceProperty: 'tables_html', mutual: true), buttonGroup: group, enabled: bind { model.tables })
                                radioButton(text: 'Convert to code block', selected: bind(source: model, sourceProperty: 'tables_codeblock', mutual: true), buttonGroup: group, enabled: bind { model.tables })
                                radioButton(text: 'Markdown extra', selected: bind(source: model, sourceProperty: 'tables_extra', mutual: true), buttonGroup: group, enabled: bind { model.tables })
                                radioButton(text: 'Multi markdown', selected: bind(source: model, sourceProperty: 'tables_multi', mutual: true), buttonGroup: group, enabled: bind { model.tables })
                            }
                        }
                        checkBox(text: 'Header Ids', selected: bind(source: model, sourceProperty: 'headerIds', mutual: true))
                        checkBox(text: 'Reverse HTML Smart Quotes', selected: bind(source: model, sourceProperty: 'reverseHtmlSmartQuotes', mutual: true))
                        checkBox(text: 'Reverse HTML Smart Punctuation', selected: bind(source: model, sourceProperty: 'reverseHtmlSmartPunctuation', mutual: true))
                        checkBox(text: 'Reverse Unicode Smart Quotes', selected: bind(source: model, sourceProperty: 'reverseUnicodeSmartQuotes', mutual: true))
                        checkBox(text: 'Reverse Unicode Smart Punctuation', selected: bind(source: model, sourceProperty: 'reverseUnicodeSmartPunctuation', mutual: true))
                        button(text: 'Convert', actionPerformed: controller.&convert, constraints: 'h 60')
                    }
                }
                panel {
                    migLayout(layoutConstraints: '', rowConstraints: '[][fill,grow]', columnConstraints: '[fill,grow][][]')
                    label('Markdown')
                    button('Load', actionPerformed: controller.&loadMarkdown, constraints: 'right')
                    button('Save', actionPerformed: controller.&saveMarkdown, constraints: 'right, wrap')
                    rtextScrollPane(constraints: 'spanx 3') {
                        rtextArea(id: 'target', text: bind(source: model, sourceProperty: 'target', mutual: true)) {
                            keyStrokeAction(action: action(closure: controller.&openFind), keyStroke: 'ctrl F')
                        }
                    }
                    bind(source: model, sourceProperty: 'target', target: model, targetProperty: 'htmlTarget', converter: { controller.pegDown(it) })
                }
            }
            panel {
                migLayout(layoutConstraints: 'flowy', rowConstraints: '[][fill,grow]', columnConstraints: '[fill,grow]')
                label('Preview')
                Logger.getLogger("org.lobobrowser").setLevel(Level.WARNING);
                def htmlPanel = new HtmlPanel()
                UserAgentContext ucontext = new SimpleUserAgentContext();
                SimpleHtmlRendererContext rcontext = new SimpleHtmlRendererContext(htmlPanel, ucontext);
                DocumentBuilderImpl dbi = new DocumentBuilderImpl(ucontext, rcontext);
                widget(htmlPanel, id: 'htmlTarget')
                model.addPropertyChangeListener('htmlTarget', { PropertyChangeEvent evt ->
                    try {
                        String html = """<html>
                                            <head>
                                                <style type='text/css'>
                                                    $css
                                                </style>
                                            </head>
                                            <body>
                                                ${evt.newValue ?: ''}
                                            </body>
                                        </html>"""
                        htmlPanel.setHtml(html, 'http://localhost/markdown.html', rcontext);
                    } catch (Exception e) {
                        def sw = new StringWriter()
                        e.printStackTrace(new PrintWriter(sw))
                        model.errors = sw.toString()
                    }
                } as PropertyChangeListener)
            }
        }
        scrollPane {
            textArea(id: 'errors', text: bind(source: model, sourceProperty: 'errors'), font: new Font('Monospaced', Font.BOLD, 12))
        }
    }
}
