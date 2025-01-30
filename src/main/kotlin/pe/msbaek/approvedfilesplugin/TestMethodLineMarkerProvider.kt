package pe.msbaek.approvedfilesplugin
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder

class TestMethodLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        // PsiMethod인지 확인
        if (element !is PsiMethod) {
            return
        }

        // @Test 어노테이션이 있는지 확인
        element.modifierList.annotations.forEach { annotation ->
            if (annotation.qualifiedName == "org.junit.jupiter.api.Test") {
                val builder = NavigationGutterIconBuilder.create(Icons.ApprovedIcon)
                    .setTarget(element)
                    .setTooltipText("View approved file")

                result.add(builder.createLineMarkerInfo(element.nameIdentifier ?: element))
            }
        }
    }
}