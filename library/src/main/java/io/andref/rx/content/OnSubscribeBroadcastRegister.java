/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.andref.rx.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

class OnSubscribeBroadcastRegister implements Observable.OnSubscribe<Intent>
{
    private final Context mContext;
    private final IntentFilter mIntentFilter;
    private final String mBroadcastPermission;
    private final Handler mSchedulerHandler;

    public OnSubscribeBroadcastRegister(Context context, IntentFilter intentFilter, String broadcastPermission, Handler schedulerHandler)
    {
        mContext = context;
        mIntentFilter = intentFilter;
        mBroadcastPermission = broadcastPermission;
        mSchedulerHandler = schedulerHandler;
    }

    @Override
    public void call(final Subscriber<? super Intent> subscriber)
    {
        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                subscriber.onNext(intent);
            }
        };

        final Subscription subscription = Subscriptions.create(new Action0()
        {
            @Override
            public void call()
            {
                mContext.unregisterReceiver(broadcastReceiver);
            }
        });

        subscriber.add(subscription);
        mContext.registerReceiver(broadcastReceiver, mIntentFilter, mBroadcastPermission, mSchedulerHandler);
    }
}