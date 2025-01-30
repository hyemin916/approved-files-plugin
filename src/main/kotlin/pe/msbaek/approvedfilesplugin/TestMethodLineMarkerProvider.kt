package pe.msbaek.approvedfilesplugin

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import java.awt.event.MouseEvent

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

        return object : LineMarkerInfo<PsiElement>(
            element.nameIdentifier ?: element,
            (element.nameIdentifier ?: element).textRange,
            Icons.ApprovedIcon,
            { createHoverHandler(approvedFile) },
            { event, _ -> handleClick(event, approvedFile, element.project) },
            GutterIconRenderer.Alignment.RIGHT,
            { "View approved file" }
        ) {}
    }

    private fun createHoverHandler(approvedFile: com.intellij.openapi.vfs.VirtualFile?): String {
        return if (approvedFile != null) {
            try {
                FileDocumentManager.getInstance().getDocument(approvedFile)?.text ?: "Unable to read file content"
            } catch (e: Exception) {
                "Error reading file: ${e.message}"
            }
        } else {
            "Approved file not found"
        }
    }

    private fun handleClick(
        event: MouseEvent,
        approvedFile: com.intellij.openapi.vfs.VirtualFile?,
        project: Project
    ): Boolean {
        if (approvedFile != null) {
            OpenFileDescriptor(project, approvedFile).navigate(true)
        } else {
            showNotification(project, "Approved file not found")
        }
        return true
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