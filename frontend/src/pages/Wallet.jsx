import { useEffect, useState } from "react";
import { ArrowDownLeft, ArrowUpRight, ShieldCheck, WalletCards } from "lucide-react";

import api from "../services/api";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";

function Wallet() {
  const [wallet, setWallet] = useState(null);

  useEffect(() => {
    api.get("/wallets/me")
      .then((res) => setWallet(res.data))
      .catch((err) => console.error(err));
  }, []);

  if (!wallet) {
    return <p className="text-slate-500">Loading wallet...</p>;
  }

  return (
    <div className="space-y-8">
      <div>
        <Badge className="mb-3 bg-blue-100 text-blue-700 hover:bg-blue-100">
          PayFlow Wallet
        </Badge>
        <h1 className="text-4xl font-bold tracking-tight text-slate-950">
          Business Wallet
        </h1>
        <p className="text-slate-500 mt-2">
          Manage your balance, funds, and wallet identity.
        </p>
      </div>

      <div className="grid gap-6 xl:grid-cols-3">
        <Card className="xl:col-span-2 overflow-hidden border-0 payflow-gradient text-white shadow-2xl rounded-[2rem] relative">
          <div className="absolute -right-20 -top-20 h-60 w-60 rounded-full bg-cyan-300/20 blur-3xl" />
          <div className="absolute -left-20 -bottom-20 h-60 w-60 rounded-full bg-blue-500/20 blur-3xl" />

          <CardContent className="relative p-8">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-cyan-100/80 text-sm">PayFlow Business</p>
                <h2 className="text-5xl font-black mt-4">
                  {wallet.balance} {wallet.currency}
                </h2>
              </div>

              <div className="h-14 w-14 rounded-3xl bg-white/10 border border-white/15 flex items-center justify-center">
                <WalletCards size={26} />
              </div>
            </div>

            <div className="mt-16 grid grid-cols-2 gap-6">
              <div>
                <p className="text-xs text-cyan-100/60">Wallet ID</p>
                <p className="font-semibold">PFW-{wallet.id}-2026</p>
              </div>

              <div>
                <p className="text-xs text-cyan-100/60">Status</p>
                <p className="font-semibold">{wallet.status}</p>
              </div>
            </div>

            <div className="mt-8 h-px flow-line" />

            <div className="mt-6 flex items-center justify-between">
              <p className="text-sm text-cyan-100/70">
                Secured wallet infrastructure for invoices and payments.
              </p>

              <Badge className="bg-emerald-400/20 text-emerald-300 hover:bg-emerald-400/20">
                Active
              </Badge>
            </div>
          </CardContent>
        </Card>

        <Card className="payflow-card rounded-[2rem]">
          <CardContent className="p-6 space-y-4">
            <h3 className="text-xl font-bold">Quick Wallet Actions</h3>

            <Button className="w-full h-14 rounded-2xl justify-start gap-3 bg-[#07111F] hover:bg-[#0b1730]">
              <ArrowDownLeft size={18} />
              Deposit Funds
            </Button>

            <Button variant="outline" className="w-full h-14 rounded-2xl justify-start gap-3">
              <ArrowUpRight size={18} />
              Send Money
            </Button>

            <div className="rounded-2xl bg-slate-50 border p-4 flex gap-3">
              <ShieldCheck className="text-emerald-500" size={22} />
              <div>
                <p className="font-semibold text-sm">Protected by JWT</p>
                <p className="text-xs text-slate-500 mt-1">
                  Every wallet request is authenticated through your Spring Boot security layer.
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

export default Wallet;