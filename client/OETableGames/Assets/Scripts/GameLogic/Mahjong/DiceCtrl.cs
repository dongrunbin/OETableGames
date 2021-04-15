using DG.Tweening;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class DiceCtrl : MonoBehaviour
{
    private int m_PlayTimes;

    private static Dictionary<int, Vector3> s_AngleDic;


    private Queue<Vector3> m_AngleQueue = new Queue<Vector3>();

    static DiceCtrl()
    {
        s_AngleDic = new Dictionary<int, Vector3>();
        s_AngleDic.Add(1, new Vector3(0, 0, 180));
        s_AngleDic.Add(2, new Vector3(0, 0, 90));
        s_AngleDic.Add(3, new Vector3(-90, 0, 180));
        s_AngleDic.Add(4, new Vector3(0, 0, -90));
        s_AngleDic.Add(5, new Vector3(90, 0, 0));
        s_AngleDic.Add(6, new Vector3(0, 0, 0));
    }

    public IEnumerator RollAnimation(int dice)
    {
        for (int i = 0; i < 4; ++i)
        {
            transform.DOLocalMove(GameUtil.GetRandomPos(Vector3.zero, 1f), 0.2f);
            transform.DORotate(new Vector3(UnityEngine.Random.Range(720, 1080), UnityEngine.Random.Range(720, 1080), UnityEngine.Random.Range(720, 1080)), 0.2f, RotateMode.FastBeyond360);
            yield return new WaitForSeconds(0.2f);
        }
        transform.DOLocalMove(GameUtil.GetRandomPos(Vector3.zero, 1f), 0.2f);
        transform.DORotate(s_AngleDic[dice], 0.2f, RotateMode.FastBeyond360);
        yield return new WaitForSeconds(0.9f);
        Destroy(gameObject);
        yield return null;
    }

    public void Roll(int diceNumber, int diceNumber2)
    {
        m_PlayTimes = 0;
        m_AngleQueue.Enqueue(s_AngleDic[diceNumber2]);
        PlayRollAnimation(OnAnimationComplete);
    }

    private void OnAnimationComplete()
    {
        ++m_PlayTimes;
        if (m_PlayTimes > 2)
        {
            PlayAnimationLast();
            return;
        }
        PlayRollAnimation(OnAnimationComplete);
    }

    private void PlayAnimationLast()
    {
        m_PlayTimes = 0;
        transform.DOLocalMove(GameUtil.GetRandomPos(Vector3.zero, 1f), 0.2f).OnComplete(() =>
        {
            if (m_AngleQueue.Count > 0)
            {
                StartCoroutine(AnimationCoroutine(OnAnimationComplete));
            }
            else
            {
                Destroy(gameObject, 2f);
            }

        });
        transform.DORotate(m_AngleQueue.Dequeue(), 0.2f, RotateMode.FastBeyond360);
    }

    private void PlayRollAnimation(TweenCallback onComplete)
    {
        transform.DOLocalMove(GameUtil.GetRandomPos(Vector3.zero, 1f), 0.2f).OnComplete(onComplete);
        transform.DORotate(new Vector3(UnityEngine.Random.Range(720, 1080), UnityEngine.Random.Range(720, 1080), UnityEngine.Random.Range(720, 1080)), 0.2f, RotateMode.FastBeyond360);
    }

    private IEnumerator AnimationCoroutine(TweenCallback onComplete)
    {
        yield return new WaitForSeconds(1.8f);
        transform.DOLocalMove(GameUtil.GetRandomPos(Vector3.zero, 1f), 0.2f).OnComplete(onComplete);
        transform.DORotate(new Vector3(UnityEngine.Random.Range(720, 1080), UnityEngine.Random.Range(720, 1080), UnityEngine.Random.Range(720, 1080)), 0.2f, RotateMode.FastBeyond360);
    }
}
