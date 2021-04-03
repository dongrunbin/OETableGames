//===================================================
//Author      : DRB
//CreateTime  ：2021/4/3 22:50:44
//Description ：
//===================================================
using UnityEngine;

public class UIItemBase : MonoBehaviour
{
    private void Awake()
    {
        OnAwake();
    }

    private void Start()
    {
        OnStart();
    }

    private void OnDestroy()
    {
        BeforeOnDestroy();
    }

    protected virtual void OnAwake() { }

    protected virtual void OnStart() { }

    protected virtual void BeforeOnDestroy() { }


    public virtual void Show()
    {
        this.gameObject.SetActive(true);
    }

    public virtual void Hide()
    {
        this.gameObject.SetActive(false);
    }
}
