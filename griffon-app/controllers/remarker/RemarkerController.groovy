package remarker

import com.overzealous.remark.Remark
import com.overzealous.remark.Options
import org.pegdown.PegDownProcessor
import org.pegdown.Extensions
import java.util.concurrent.locks.ReentrantLock
import javax.swing.JOptionPane

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
            else if (model.file)
                target = remark.convert(new File(model.source))
            else if (model.url)
                target = remark.convert(new URL(model.source), 5000)
            target = target.replaceAll('<', '&lt;')
            target = target.replaceAll('&', '&amp;')
            model.target = target
        } catch (Exception e) {
            def sw = new StringWriter()
            e.printStackTrace(new PrintWriter(sw))
            JOptionPane.showConfirmDialog(null, sw.toString(), "An error occurred", JOptionPane.ERROR_MESSAGE)
        }
    }

    protected def pegDown(String text) {
        PegDownProcessor p = new PegDownProcessor(Extensions.ALL - Extensions.QUOTES)
        try {
            processorLock.lock()
            String result = p.markdownToHtml(text)
            //result = result.replaceAll('&rsquo;', "'")
            //result = result.replaceAll('&lsquo;', "'")
            return result
        } finally {
            processorLock.unlock()
        }
    }
    
    
}
