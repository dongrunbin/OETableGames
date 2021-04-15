//===================================================
//Author      : DRB
//CreateTime  ：2021/4/14 2:16:57
//Description ：
//===================================================
using UnityEngine;

public class HandCtrl : MonoBehaviour
{
    private Animator Animator;

    private void Awake()
    {
        Animator = GetComponentInChildren<Animator>();
    }

    private void Update()
    {
        AnimatorStateInfo info = Animator.GetCurrentAnimatorStateInfo(0);
        if (info.IsName("Animation"))
        {
            if (info.normalizedTime >= 1f)
            {
                gameObject.SetActive(false);
            }
        }
    }

    public void Reset()
    {
        gameObject.SetActive(true);
        Animator.Play("Animation", 0, 0);
    }
}
