package remarker

import java.awt.Font
import javax.swing.JSplitPane
import javax.swing.JTabbedPane
import org.xhtmlrenderer.simple.XHTMLPanel
import javax.xml.parsers.DocumentBuilderFactory
import org.xml.sax.InputSource
import javax.swing.JOptionPane

application(title: 'remarker',
        preferredSize: [1024, 768],
        pack: true,
        //location: [50,50],
        locationByPlatform: true,
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]) {
    splitPane(orientation: JSplitPane.VERTICAL_SPLIT, dividerLocation: 500) {
        panel {
            migLayout(layoutConstraints: 'wrap 1, fill', rowConstraints: '[][fill, grow][]', columnConstraints: '[fill, grow]')
            panel {
                migLayout(layoutConstraints: 'fill', rowConstraints: '[fill,grow]', columnConstraints: '[fill,grow]')
                buttonGroup().with { group ->
                    radioButton(text: 'HTML Fragment', selected: bind(source: model, sourceProperty: 'fragment', mutual: true), buttonGroup: group)
                    radioButton(text: 'Full HTML', selected: bind(source: model, sourceProperty: 'html', mutual: true), buttonGroup: group)
                    radioButton(text: 'File', selected: bind(source: model, sourceProperty: 'file', mutual: true), buttonGroup: group)
                    radioButton(text: 'URL', selected: bind(source: model, sourceProperty: 'url', mutual: true), buttonGroup: group)
                }
            }
            scrollPane {
                textArea(id: 'source', text: bind(source: model, sourceProperty: 'source', mutual: true), font: new Font('Monospaced', Font.BOLD, 12))
            }
            panel {
                migLayout(layoutConstraints: 'wrap 6, fill, flowy', rowConstraints: '[fill,grow]', columnConstraints: '[fill,grow]')
                checkBox(text: 'Hardwraps', selected: bind(source: model, sourceProperty: 'hardwraps', mutual: true))
                checkBox(text: 'Auto Links', selected: bind(source: model, sourceProperty: 'autoLinks', mutual: true))
                checkBox(text: 'Abbreviations', selected: bind(source: model, sourceProperty: 'abbreviations', mutual: true))
                checkBox(text: 'Definition Lists', selected: bind(source: model, sourceProperty: 'definitionLists', mutual: true))
                checkBox(text: 'Simple Link Ids', selected: bind(source: model, sourceProperty: 'simpleLinkIds', mutual: true))
                panel {
                    migLayout(layoutConstraints: 'wrap 2', rowConstraints: '5[]0[]', columnConstraints: '0[]0[]')
                    checkBox(text: 'Inline Links', selected: bind(source: model, sourceProperty: 'inlineLinks', mutual: true))
                    label('')
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
                button(text: 'Convert', actionPerformed: controller.&convert)
            }
        }
        tabbedPane(tabPlacement: JTabbedPane.BOTTOM) {
            scrollPane(name: 'Markdown') {
                textArea(id: 'target', text: bind(source: model, sourceProperty: 'target', mutual: true), font: new Font('Monospaced', Font.BOLD, 12))
            }
            scrollPane(name: 'HTML') {
                textArea(id: 'htmlTarget', text: bind(source: model, sourceProperty: 'htmlTarget'), font: new Font('Monospaced', Font.BOLD, 12))
            }
            scrollPane(name: 'Preview') {
                def xhtml = new XHTMLPanel()
                def docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                widget(xhtml, id: 'htmlTarget', document: bind(source: model, sourceProperty: 'htmlTarget', converter: {
                    try {
                        docBuilder.parse(new InputSource(new StringReader("<html><head></head><body>$it</body></html>")))
                    } catch (Exception e) {
                        def sw = new StringWriter()
                        e.printStackTrace(new PrintWriter(sw))
                        JOptionPane.showConfirmDialog(null, sw.toString(), "An error occurred", JOptionPane.ERROR_MESSAGE)
                    }
                }))
            }
        }

        bind(source: model, sourceProperty: 'target', target: model, targetProperty: 'htmlTarget', converter: { controller.pegDown(it) })
    }
}
