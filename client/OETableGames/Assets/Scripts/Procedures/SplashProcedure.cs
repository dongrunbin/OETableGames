//===================================================
//Author      : DRB
//CreateTime  ：2021/2/25 12:50:43
//Description ：
//===================================================
using DrbFramework.Internal;
using DrbFramework.Procedure;
using DrbFramework.UI;

public class SplashProcedure : Procedure
{
    private IUIForm m_SplashForm;

    public override void OnEnter(object userData)
    {
        base.OnEnter(userData);

        m_SplashForm = DrbComponent.UISystem.OpenInternalForm("UI/Forms/SplashForm", "BackGround");
    }




    public override void OnLeave()
    {
        base.OnLeave();
        if (m_SplashForm != null)
        {
            DrbComponent.UISystem.DestroyForm(m_SplashForm);
        }
    }
}
