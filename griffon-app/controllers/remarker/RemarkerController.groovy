package remarker

import com.overzealous.remark.Remark
import com.overzealous.remark.Options
import org.pegdown.PegDownProcessor
import org.pegdown.Extensions
import java.util.concurrent.locks.ReentrantLock
import javax.swing.JFileChooser
import java.util.prefs.Preferences
import javax.swing.JOptionPane
import groovy.beans.Bindable
import org.fife.ui.rtextarea.SearchEngine
import java.util.regex.PatternSyntaxException
import javax.swing.text.DefaultCaret
import java.awt.Point
import java.awt.Rectangle
import java.awt.Dimension

class RemarkerController {
    def model
    def view

    private final ReentrantLock processorLock = new ReentrantLock()

    def convert = { evt = null ->
        Options options = new Options()

        options.hardwraps = model.hardwraps
        options.autoLinks = model.autoLinks
        options.abbreviations = model.abbreviations
        options.definitionLists = model.definitionLists
        options.reverseHtmlSmartQuotes = model.reverseHtmlSmartQuotes
        options.reverseHtmlSmartPunctuation = model.reverseHtmlSmartPunctuation
        options.reverseUnicodeSmartPunctuation = model.reverseUnicodeSmartPunctuation
        options.reverseUnicodeSmartQuotes = model.reverseUnicodeSmartQuotes
        options.fencedCodeBlocks = Options.FencedCodeBlocks.DISABLED
        if (model.fenced_backtick)
            options.fencedCodeBlocks = Options.FencedCodeBlocks.ENABLED_BACKTICK
        else if (model.fenced_tilde)
            options.fencedCodeBlocks = Options.FencedCodeBlocks.ENABLED_TILDE
        options.tables = Options.Tables.LEAVE_AS_HTML
        if (model.tables_codeblock)
            options.tables = Options.Tables.CONVERT_TO_CODE_BLOCK
        else if (model.tables_remove)
            options.tables = Options.Tables.REMOVE
        else if (model.tables_extra)
            options.tables = Options.Tables.MARKDOWN_EXTRA
        else if (model.tables_multi)
            options.tables = Options.Tables.MULTI_MARKDOWN
        options.inWordEmphasis = Options.InWordEmphasis.NORMAL
        if (model.emphasis_spaces)
            options.inWordEmphasis = Options.InWordEmphasis.ADD_SPACES
        else if (model.emphasis_remove)
            options.inWordEmphasis = Options.InWordEmphasis.REMOVE_EMPHASIS
        options.headerIds = model.headerIds
        options.simpleLinkIds = model.simpleLinkIds
        options.inlineLinks = model.inlineLinks

        Remark remark = new Remark(options)

        String target
        try {
            if (model.fragment)
                target = remark.convertFragment(model.source)
            else if (model.html)
                target = remark.convert(model.source)
            else if (model.url)
                target = remark.convert(new URL(model.source), 5000)
            target = target.replaceAll('&', '&amp;')
            target = target.replaceAll('<', '&lt;')
            model.target = target
        } catch (Exception e) {
            def sw = new StringWriter()
            e.printStackTrace(new PrintWriter(sw))
            model.errors = sw.toString()
        }
    }

    protected def pegDown(String text) {
        PegDownProcessor p = new PegDownProcessor(Extensions.ALL - Extensions.QUOTES)
        try {
            processorLock.lock()
            String result = p.markdownToHtml(text)
            return result
        } finally {
            processorLock.unlock()
        }
    }

    Preferences prefs = Preferences.userRoot().node('remarker')

    def loadMarkdown = { evt ->
        withFileChooser('Markdown') { File file ->
            model.target = file.text
        }
    }

    def saveMarkdown = { evt ->
        withFileChooser('Markdown', true) { File file ->
            if (file.exists()) {
                if (JOptionPane.showConfirmDialog(null, "File '${file.absolutePath}' exists.\nShould it be overwritten?", 'Overwrite file') != JOptionPane.YES_OPTION)
                    return
            }
            file.text = model.target
        }
    }

    def loadHTML = { evt ->
        withFileChooser('HTML') { File file ->
            model.source = file.text
        }
    }

    protected withFileChooser(String type, boolean save = false, Closure closure) {
        String key = "last${type}"
        String oldFile = prefs.get(key, null)
        if (oldFile) view.openFile.selectedFile = new File(oldFile)
        if (save) {
            if (view.openFile.showSaveDialog() != JFileChooser.APPROVE_OPTION)
                return
        } else {
            if (view.openFile.showOpenDialog() != JFileChooser.APPROVE_OPTION)
                return
        }
        File file = view.openFile.selectedFile
        closure.call(file)
        if (!file)
            return
        prefs.put(key, file.absolutePath)
    }

    def searchText = { evt = null ->
        try {
            execSync {
                SearchEngine.find(view.target, model.find, !model.backwards, model.caseSensitive, model.wholeWord, model.regexp)
                view.target.grabFocus()
            }
        } catch (PatternSyntaxException ex) {
            JOptionPane.showMessageDialog(view.target, "'$model.find' is not a valid regular expression pattern")
        }
    }

    def replace = { evt = null ->
        try {
            execSync {
                SearchEngine.replace(view.target, model.find, model.replace, !model.backwards, model.caseSensitive, model.wholeWord, model.regexp)
                view.target.grabFocus()
            }
            searchText()
        } catch (PatternSyntaxException ex) {
            JOptionPane.showMessageDialog(view.target, "'$model.find' is not a valid regular expression pattern")
        }
    }

    def replaceAll = { evt = null ->
        try {
            execSync {
                SearchEngine.replaceAll(view.target, model.find, model.replace, model.caseSensitive, model.wholeWord, model.regexp)
                view.target.grabFocus()
            }
        } catch (PatternSyntaxException ex) {
            JOptionPane.showMessageDialog(view.target, "'$model.find' is not a valid regular expression pattern")
        }
    }

    def openFind = { evt ->
        def sel = view.target.selectedText
        if (sel)
            model.find = sel
        view.findDialog.pack()
        view.findDialog.show()
        view.findField.requestFocus()
    }
}
