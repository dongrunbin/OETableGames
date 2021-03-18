//===================================================
//Author      : DRB
//CreateTime  ：2021/2/25 15:04:59
//Description ：
//===================================================

using UnityEngine;
using UnityEngine.UI;

public class InitForm : UGUIForm
{
    [SerializeField]
    private Text m_Text;
    [SerializeField]
    private Slider m_Slider;


    public void SetUI(int currentCount, int totalCount, int currentSize, int totalSize)
    {
        m_Text.text = string.Format("已完成{0}/{1}个文件，{2}/{3}KB", currentCount, totalCount, currentSize, totalSize);
        m_Slider.value = (float)currentSize / totalSize;
    }
}
