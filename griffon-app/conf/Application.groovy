application {
    title = 'Remarker'
    startupGroups = ['remarker']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "remarker"
    'remarker' {
        model      = 'remarker.RemarkerModel'
        view       = 'remarker.RemarkerView'
        controller = 'remarker.RemarkerController'
    }

}
