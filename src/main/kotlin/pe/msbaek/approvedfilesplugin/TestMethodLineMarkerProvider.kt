package pe.msbaek.approvedfilesplugin

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import java.awt.event.MouseEvent
import java.awt.Dimension
import javax.swing.JPanel
import java.awt.BorderLayout
import com.intellij.ui.components.JBScrollPane
import com.intellij.openapi.editor.markup.GutterIconRenderer

class TestMethodLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is PsiMethod) {
            return null
        }

        val hasTestAnnotation = element.modifierList.annotations.any {
            it.qualifiedName == "org.junit.jupiter.api.Test"
        }

        if (!hasTestAnnotation) {
            return null
        }

        val approvedFile = ApprovedFileFinder.findApprovedFile(element)
        val className = element.containingClass?.name
        val methodName = element.name

        val tooltipText = if (approvedFile != null)
            "View approved file: ${approvedFile.name}"
        else
            "Approved file not found: $className.$methodName.approved.txt"

        return object : LineMarkerInfo<PsiElement>(
            element.nameIdentifier ?: element,
            (element.nameIdentifier ?: element).textRange,
            Icons.ApprovedIcon,
            { tooltipText },
            { event, elt ->
                if (approvedFile != null) {
                    showFileContentPopup(approvedFile, event)
                } else {
                    showNotification(elt.project, tooltipText)
                }
            },
            GutterIconRenderer.Alignment.RIGHT
        ) {}
    }

    private fun showFileContentPopup(virtualFile: com.intellij.openapi.vfs.VirtualFile, mouseEvent: MouseEvent) {
        val document = FileDocumentManager.getInstance().getDocument(virtualFile)
        if (document != null) {
            val editorFactory = EditorFactory.getInstance()
            val editor = editorFactory.createEditor(document)

            val panel = JPanel(BorderLayout())
            panel.add(JBScrollPane(editor.component), BorderLayout.CENTER)

            JBPopupFactory.getInstance()
                .createComponentPopupBuilder(panel, editor.component)
                .setProject(editor.project)
                .setTitle(virtualFile.name)
                .setMovable(true)
                .setResizable(true)
                .setMinSize(Dimension(400, 200))
                .createPopup()
                .show(RelativePoint(mouseEvent))
        }
    }

    private fun showNotification(project: Project, content: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Approved Files Plugin")
            .createNotification(
                "Approved File Not Found",
                content,
                NotificationType.WARNING
            )
            .notify(project)
    }
}