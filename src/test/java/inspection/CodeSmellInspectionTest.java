package inspection;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CodeSmellInspectionTest extends LightJavaCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/resources";
    }

    /**
     * コードインスペクションが検出したコードを特定
     *
     * @param inspectionShortName コードインスペクションのID
     * @return コードインスペクションの検出結果
     */
    protected List<HighlightInfo> getHighlightInfoList(final String inspectionShortName) {
        List<HighlightInfo> highlightInfoList = myFixture.doHighlighting();
        List<HighlightInfo> returnValue = new ArrayList<>();

        for (HighlightInfo highlightInfo : highlightInfoList) {
            if (inspectionShortName.equals(highlightInfo.getInspectionToolId())) {
                returnValue.add(highlightInfo);
            }
        }
        assertTrue(returnValue.size() > 0);

        return returnValue;
    }

    /**
     * コードインスペクションのハイライト表示テスト
     *
     * このメソッドにコードインスペクションのハイライト表示のテストコードを書く
     */
    public void testForInspection() {
    }

    public void testCreateOptionUI() {
        CodeSmellInspection inspection = new CodeSmellInspection() {
            @Override
            protected JComponent createOptionUI(String description, @NotNull InspectionData data) {
                return super.createOptionUI(description, data);
            }
        };
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);
        inspection.createOptionUI("test", inspectionData);
    }

    public void testGetDisplayName() {
        CodeSmellInspection inspection = new CodeSmellInspection() {
            @NotNull
            @Override
            public String getDisplayName() {
                return super.getDisplayName();
            }
        };

        // displayNameはnullなので必ず例外が発生する
        try {
            inspection.getDisplayName();
            fail();
        } catch (IllegalStateException ignored) {
        }
    }

    public void testGetGroupDisplayName() {
        CodeSmellInspection inspection = new CodeSmellInspection() {
            @NotNull
            @Override
            public String getGroupDisplayName() {
                return super.getGroupDisplayName();
            }
        };
        assertEquals("Code smell", inspection.getGroupDisplayName());
    }

    public void testIsEnabledByDefault() {
        CodeSmellInspection inspection = new CodeSmellInspection() {
            @Override
            public boolean isEnabledByDefault() {
                return super.isEnabledByDefault();
            }
        };
        assertFalse(inspection.isEnabledByDefault());
    }

    public void testGetDefaultLevel() {
        CodeSmellInspection inspection = new CodeSmellInspection() {
            @NotNull
            @Override
            public HighlightDisplayLevel getDefaultLevel() {
                return super.getDefaultLevel();
            }
        };
        assertEquals(HighlightDisplayLevel.WARNING, inspection.getDefaultLevel());
    }
}
