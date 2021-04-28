//===================================================
//Author      : DRB
//CreateTime  ：2021/4/14 2:18:16
//Description ：
//===================================================
using DG.Tweening;
using DrbFramework.Extensions;
using System.Collections.Generic;
using UnityEngine;

public class Combination3D
{
    public List<MahjongCtrl> MahjongList;

    public OperationType OperationType;

    public int SubType;
    public Combination3D(OperationType operationId, int subType, List<MahjongCtrl> majiang)
    {
        OperationType = operationId;
        SubType = subType;
        MahjongList = majiang;
    }

    public void BuGang(List<MahjongCtrl> majiang)
    {
        OperationType = OperationType.Kong;
        MahjongList = majiang;
    }
}

public class CombinationCtrl : MonoBehaviour
{
    public Combination3D Combination;
    [SerializeField]
    private Transform[] m_Containers;

    [HideInInspector]
    public int MahjongCount;

    /// <summary>
    /// 动画起始点
    /// </summary>
    [SerializeField]
    private Transform[] m_AnimationSrc;

    public void SetUI(Combination3D combination, Seat seat, bool isPlayAnimation = true)
    {
        Combination = combination;
        for (int i = 0; i < combination.MahjongList.Count; ++i)
        {
            combination.MahjongList[i].gameObject.transform.SetParent(m_Containers[i]);
            combination.MahjongList[i].gameObject.SetLayer(m_Containers[i].gameObject.layer);
            if (isPlayAnimation)
            {
                combination.MahjongList[i].transform.position = m_AnimationSrc[i].position;
                combination.MahjongList[i].transform.rotation = m_AnimationSrc[i].rotation;
            }
            else
            {
                combination.MahjongList[i].transform.position = m_Containers[i].position;
                combination.MahjongList[i].transform.rotation = m_Containers[i].rotation;
            }

            if (combination.OperationType == OperationType.Kong && (combination.SubType == (int)GangType.AnGang))
            {
                if (seat.IsPlayer && i == 3)
                {

                }
                else
                {
                    combination.MahjongList[i].transform.localEulerAngles = new Vector3(180f, 0f, 0f);
                }
            }
            combination.MahjongList[i].transform.DOMove(m_Containers[i].position, 0.3f);
        }
    }

    public void Reset()
    {
        Combination = null;
        MahjongCount = 0;
    }
}
