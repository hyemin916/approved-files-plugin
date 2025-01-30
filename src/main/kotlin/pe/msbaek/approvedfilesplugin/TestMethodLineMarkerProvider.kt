package pe.msbaek.approvedfilesplugin

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager

class TestMethodLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (element !is PsiMethod) {
            return
        }

        element.modifierList.annotations.forEach { annotation ->
            if (annotation.qualifiedName == "org.junit.jupiter.api.Test") {
                val approvedFile = ApprovedFileFinder.findApprovedFile(element)
                val className = element.containingClass?.name
                val methodName = element.name

                val tooltipText = if (approvedFile != null)
                    "View approved file: ${approvedFile.name}"
                else
                    "Approved file not found: $className.$methodName.approved.txt"

                val builder = NavigationGutterIconBuilder.create(Icons.ApprovedIcon)
                    .setTooltipText(tooltipText)

                if (approvedFile != null) {
                    val psiFile = PsiManager.getInstance(element.project).findFile(approvedFile)
                    if (psiFile != null) {
                        builder.setTarget(psiFile)
                    }
                }

                val markerInfo = builder.createLineMarkerInfo(element.nameIdentifier ?: element)

                if (approvedFile == null) {
                    showNotification(element.project, tooltipText)
                }

                result.add(markerInfo)
            }
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
