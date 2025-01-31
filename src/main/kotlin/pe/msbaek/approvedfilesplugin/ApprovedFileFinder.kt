package pe.msbaek.approvedfilesplugin
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiMethod

class ApprovedFileFinder {
    companion object {
        fun findApprovedFile(method: PsiMethod): VirtualFile? {
            val containingFile = method.containingFile.virtualFile
            val directory = containingFile.parent
            val className = method.containingClass?.name ?: return null
            val methodName = method.name

            val approvedFileName = "$className.$methodName.approved.txt"
            return directory.findChild(approvedFileName)
        }
    }
}